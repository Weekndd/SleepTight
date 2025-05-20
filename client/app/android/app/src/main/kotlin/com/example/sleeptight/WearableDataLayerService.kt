package com.example.sleeptight

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import io.flutter.plugin.common.MethodChannel
import org.json.JSONObject
import kotlinx.coroutines.*
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Wear OS Data Layer API와 통신하는 서비스
 * Flutter 앱과 연결하여 워치앱과 메시지를 주고받는 역할을 담당합니다.
 */
class WearableDataLayerService(private val context: Context) : MessageClient.OnMessageReceivedListener, DataClient.OnDataChangedListener {

    private val TAG = "WearableDataLayer"
    private val scope = CoroutineScope(Dispatchers.Main)
    
    private val nodeClient = Wearable.getNodeClient(context)
    private val messageClient = Wearable.getMessageClient(context)
    private val dataClient = Wearable.getDataClient(context)
    
    // 메시지 콜백을 저장할 맵 (메시지 경로별로 분류)
    private val messageCallbacks = mutableMapOf<String, MethodChannel.Result>()
    
    // ISO8601 형식 날짜 포맷터
    private val iso8601Format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    /**
     * 서비스 초기화 및 리스너 등록
     */
    fun initialize() {
        Log.d(TAG, "WearableDataLayerService 초기화 시작 - 리스너 등록 전")
        try {
            Wearable.getMessageClient(context).addListener(this)
            Log.d(TAG, "메시지 리스너 등록 완료")
            
            Wearable.getDataClient(context).addListener(this)
            Log.d(TAG, "데이터 리스너 등록 완료")
            
            Log.d(TAG, "WearableDataLayerService 초기화 완료")
        } catch (e: Exception) {
            Log.e(TAG, "리스너 등록 중 오류 발생", e)
        }
    }

    /**
     * 리소스 해제
     */
    fun dispose() {
        Log.d(TAG, "WearableDataLayerService 리소스 해제")
        Wearable.getMessageClient(context).removeListener(this)
        Wearable.getDataClient(context).removeListener(this)
        scope.cancel()
    }
    
    /**
     * 모든 연결된 노드(기기) 목록 가져오기
     */
    fun getConnectedNodes(result: MethodChannel.Result) {
        scope.launch {
            try {
                val nodes = withContext(Dispatchers.IO) {
                    Tasks.await(nodeClient.connectedNodes)
                }
                
                Log.d(TAG, "연결된 노드: ${nodes.size}개")
                val nodesList = mutableListOf<Map<String, String>>()
                
                nodes.forEach { node ->
                    Log.d(TAG, "노드 ID: ${node.id}, 표시명: ${node.displayName}")
                    // 명시적으로 모든 값을 String으로 변환하여 Flutter에서 타입 변환 오류 방지
                    nodesList.add(mapOf(
                        "id" to node.id.toString(),
                        "displayName" to node.displayName.toString(),
                        "isNearby" to node.isNearby.toString()
                    ))
                }
                
                result.success(nodesList)
            } catch (e: Exception) {
                Log.e(TAG, "연결된 노드 가져오기 실패", e)
                result.error("NODES_ERROR", "연결된 노드 가져오기 실패", e.message)
            }
        }
    }
    
    /**
     * 메시지 전송
     */
    fun sendMessage(nodeId: String, path: String, data: ByteArray, result: MethodChannel.Result) {
        scope.launch {
            try {
                Log.d(TAG, "메시지 전송: 노드=$nodeId, 경로=$path")
                val taskResult = withContext(Dispatchers.IO) {
                    Tasks.await(messageClient.sendMessage(nodeId, path, data))
                }
                
                // 메시지 경로별로 콜백 저장 (결과 받기 위해)
                messageCallbacks[path] = result
                
                Log.d(TAG, "메시지 전송 성공: $taskResult")
                // 결과는 메시지 응답이 오면 처리
            } catch (e: Exception) {
                Log.e(TAG, "메시지 전송 실패", e)
                result.error("MESSAGE_ERROR", "메시지 전송 실패", e.message)
            }
        }
    }
    
    /**
     * 헬스 데이터 요청 메시지
     */
    fun requestHealthData(result: MethodChannel.Result) {
        scope.launch {
            try {
                val nodes = withContext(Dispatchers.IO) {
                    Tasks.await(nodeClient.connectedNodes)
                }
                
                if (nodes.isEmpty()) {
                    Log.w(TAG, "연결된 노드가 없습니다")
                    result.error("NO_NODES", "연결된 워치 기기가 없습니다", null)
                    return@launch
                }
                
                // 첫 번째 노드(기기)에 메시지 전송
                val firstNode = nodes.first()
                Log.d(TAG, "헬스 데이터 요청: ${firstNode.id} (${firstNode.displayName})")
                
                withContext(Dispatchers.IO) {
                    Tasks.await(messageClient.sendMessage(firstNode.id, "/request_health_data", byteArrayOf()))
                }
                
                // 응답을 처리할 콜백 저장
                messageCallbacks["/health_data_response"] = result
                
            } catch (e: Exception) {
                Log.e(TAG, "헬스 데이터 요청 실패", e)
                result.error("REQUEST_ERROR", "헬스 데이터 요청 실패", e.message)
            }
        }
    }
    
    /**
     * 물 섭취량 업데이트
     */
    fun updateWaterIntake(amount: Double, result: MethodChannel.Result) {
        scope.launch {
            try {
                val data = JSONObject().apply {
                    put("amount", amount)
                    put("dateTime", iso8601Format.format(Date()))
                }
                
                val nodes = withContext(Dispatchers.IO) {
                    Tasks.await(nodeClient.connectedNodes)
                }
                
                if (nodes.isEmpty()) {
                    Log.w(TAG, "연결된 노드가 없습니다")
                    result.error("NO_NODES", "연결된 워치 기기가 없습니다", null)
                    return@launch
                }
                
                // 첫 번째 노드에 메시지 전송
                val firstNode = nodes.first()
                Log.d(TAG, "물 섭취량 업데이트: ${amount}ml, 노드=${firstNode.id}")
                
                withContext(Dispatchers.IO) {
                    Tasks.await(messageClient.sendMessage(
                        firstNode.id,
                        "/update_water_intake",
                        data.toString().toByteArray()
                    ))
                }
                
                // 응답을 처리할 콜백 저장
                messageCallbacks["/update_water_intake_result"] = result
                
            } catch (e: Exception) {
                Log.e(TAG, "물 섭취량 업데이트 실패", e)
                result.error("UPDATE_ERROR", "물 섭취량 업데이트 실패", e.message)
            }
        }
    }
    
    /**
     * 카페인 섭취량 업데이트
     */
    fun updateCaffeineIntake(amount: Double, result: MethodChannel.Result) {
        scope.launch {
            try {
                val data = JSONObject().apply {
                    put("amount", amount)
                    put("dateTime", iso8601Format.format(Date()))
                }
                
                val nodes = withContext(Dispatchers.IO) {
                    Tasks.await(nodeClient.connectedNodes)
                }
                
                if (nodes.isEmpty()) {
                    Log.w(TAG, "연결된 노드가 없습니다")
                    result.error("NO_NODES", "연결된 워치 기기가 없습니다", null)
                    return@launch
                }
                
                // 첫 번째 노드에 메시지 전송
                val firstNode = nodes.first()
                Log.d(TAG, "카페인 섭취량 업데이트: ${amount}mg, 노드=${firstNode.id}")
                
                withContext(Dispatchers.IO) {
                    Tasks.await(messageClient.sendMessage(
                        firstNode.id,
                        "/update_caffeine_intake",
                        data.toString().toByteArray()
                    ))
                }
                
                // 응답을 처리할 콜백 저장
                messageCallbacks["/update_caffeine_intake_result"] = result
                
            } catch (e: Exception) {
                Log.e(TAG, "카페인 섭취량 업데이트 실패", e)
                result.error("UPDATE_ERROR", "카페인 섭취량 업데이트 실패", e.message)
            }
        }
    }

    /**
     * 메시지 수신 처리
     */
    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "📲 메시지 수신됨: path=${messageEvent.path}, 발신자=${messageEvent.sourceNodeId}, 데이터 크기=${messageEvent.data?.size ?: 0}바이트")
        val data = messageEvent.data
        
        when (messageEvent.path) {
            "/health_data_response" -> {
                try {
                    val jsonString = String(data, StandardCharsets.UTF_8)
                    Log.d(TAG, "📲 헬스 데이터 응답 수신: $jsonString")
                    
                    // Flutter로 결과 전달
                    val callback = messageCallbacks.remove("/health_data_response")
                    if (callback != null) {
                        Log.d(TAG, "📲 헬스 데이터 응답 콜백 호출")
                        callback.success(jsonString)
                    } else {
                        Log.w(TAG, "⚠️ 헬스 데이터 응답 콜백이 없습니다")
                    }
                    
                } catch (e: Exception) {
                    Log.e(TAG, "❌ 헬스 데이터 파싱 실패", e)
                    val callback = messageCallbacks.remove("/health_data_response")
                    callback?.error("PARSE_ERROR", "헬스 데이터 파싱 실패", e.message)
                }
            }
            
            "/update_water_intake_result" -> {
                try {
                    val jsonString = String(data, StandardCharsets.UTF_8)
                    Log.d(TAG, "📲 물 섭취량 업데이트 결과 수신: $jsonString")
                    
                    // Flutter로 결과 전달
                    val callback = messageCallbacks.remove("/update_water_intake_result")
                    if (callback != null) {
                        Log.d(TAG, "📲 물 섭취량 업데이트 결과 콜백 호출")
                        callback.success(jsonString)
                    } else {
                        Log.w(TAG, "⚠️ 물 섭취량 업데이트 결과 콜백이 없습니다")
                    }
                    
                } catch (e: Exception) {
                    Log.e(TAG, "❌ 물 섭취량 업데이트 결과 파싱 실패", e)
                    val callback = messageCallbacks.remove("/update_water_intake_result")
                    callback?.error("PARSE_ERROR", "물 섭취량 업데이트 결과 파싱 실패", e.message)
                }
            }
            
            "/update_caffeine_intake_result" -> {
                try {
                    val jsonString = String(data, StandardCharsets.UTF_8)
                    Log.d(TAG, "📲 카페인 섭취량 업데이트 결과 수신: $jsonString")
                    
                    // Flutter로 결과 전달
                    val callback = messageCallbacks.remove("/update_caffeine_intake_result")
                    if (callback != null) {
                        Log.d(TAG, "📲 카페인 섭취량 업데이트 결과 콜백 호출")
                        callback.success(jsonString)
                    } else {
                        Log.w(TAG, "⚠️ 카페인 섭취량 업데이트 결과 콜백이 없습니다")
                    }
                    
                } catch (e: Exception) {
                    Log.e(TAG, "❌ 카페인 섭취량 업데이트 결과 파싱 실패", e)
                    val callback = messageCallbacks.remove("/update_caffeine_intake_result")
                    callback?.error("PARSE_ERROR", "카페인 섭취량 업데이트 결과 파싱 실패", e.message)
                }
            }
            
            "/request_health_data" -> {
                Log.d(TAG, "📲 헬스 데이터 요청 수신 (워치에서)")
                // 여기서 필요한 헬스 데이터를 수집하고 응답을 보낼 수 있음
            }
            
            else -> {
                Log.d(TAG, "📲 인식할 수 없는 경로의 메시지 수신: ${messageEvent.path}")
            }
        }
    }

    /**
     * 데이터 변경 이벤트 처리 (현재는 사용하지 않지만 인터페이스 구현을 위해 필요)
     */
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        // 현재는 메시지 기반 통신만 사용
    }
} 