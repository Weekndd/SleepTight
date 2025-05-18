package com.example.wear.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.wear.data.model.HealthData
import com.google.android.gms.wearable.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

private const val TAG = "WearableRepository"
private const val PREFS_NAME = "health_data_prefs"
private const val KEY_WATER = "water_amount"
private const val KEY_CAFFEINE = "caffeine_amount"
private const val KEY_STEPS = "steps_count"
private const val KEY_CALORIES = "calories_amount"
private const val TEST_MODE = false // 테스트 모드 활성화 (실제 워치 없이 테스트할 때 true로 설정)

/**
 * 웨어러블 통신 리포지토리
 * 스마트폰 앱과 데이터를 주고받는 역할을 합니다.
 */
class WearableRepository(private val context: Context) : DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener {
    
    private val dataClient = Wearable.getDataClient(context)
    private val messageClient = Wearable.getMessageClient(context)
    private val nodeClient = Wearable.getNodeClient(context)
    
    // 코루틴 스코프 추가
    private val scope = CoroutineScope(Dispatchers.Main)
    
    // SharedPreferences 추가
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val _healthData = MutableStateFlow(loadHealthDataFromPrefs())
    val healthData: StateFlow<HealthData> = _healthData.asStateFlow()
    
    // ISO8601 형식 날짜 포맷터
    private val iso8601Format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    
    // 초기화 및 리스너 등록
    fun initialize() {
        Wearable.getDataClient(context).addListener(this)
        Wearable.getMessageClient(context).addListener(this)
    }
    
    // 리스너 등록 (이전 메서드와 호환성 유지)
    fun registerListeners() {
        initialize()
    }
    
    // 리스너 해제
    fun unregisterListeners() {
        Wearable.getDataClient(context).removeListener(this)
        Wearable.getMessageClient(context).removeListener(this)
    }
    
    // 리소스 해제
    fun destroy() {
        unregisterListeners()
        scope.cancel()
    }
    
    // SharedPreferences에서 저장된 데이터 로드
    private fun loadHealthDataFromPrefs(): HealthData {
        return HealthData(
            steps = prefs.getInt(KEY_STEPS, 0),
            calories = prefs.getInt(KEY_CALORIES, 0),
            water = prefs.getInt(KEY_WATER, 0),
            caffeine = prefs.getInt(KEY_CAFFEINE, 0),
            stepsGoal = 10000,
            caloriesGoal = 2000,
            waterGoal = 2000,
            caffeineGoal = 400
        )
    }
    
    // SharedPreferences에 데이터 저장
    private fun saveHealthDataToPrefs(healthData: HealthData) {
        prefs.edit().apply {
            putInt(KEY_STEPS, healthData.steps)
            putInt(KEY_CALORIES, healthData.calories)
            putInt(KEY_WATER, healthData.water)
            putInt(KEY_CAFFEINE, healthData.caffeine)
            apply()
        }
    }
    
    // 스마트폰에 헬스 데이터 요청
    suspend fun requestHealthData() {
        if (TEST_MODE) {
            // 테스트 모드: 더미 데이터 사용
            Log.d(TAG, "테스트 모드: 더미 데이터 로드")
            val currentData = _healthData.value
            // 기존 값 유지하면서 일부 데이터만 업데이트 (걸음수, 칼로리는 테스트용으로 랜덤값)
            val dummyData = HealthData(
                steps = (3000..8000).random(),
                calories = (500..1500).random(),
                water = currentData.water,  // 기존 물 섭취량 유지
                caffeine = currentData.caffeine, // 기존 카페인 섭취량 유지
                stepsGoal = 10000,
                caloriesGoal = 2000,
                waterGoal = 2000,
                caffeineGoal = 400
            )
            _healthData.value = dummyData
            saveHealthDataToPrefs(dummyData)
            return
        }
        
        try {
            Log.d(TAG, "연결된 노드 검색 시작...")
            val nodes = nodeClient.connectedNodes.await()
            Log.d(TAG, "연결된 노드: ${nodes.size}개")
            nodes.forEach { node -> 
                Log.d(TAG, "노드 ID: ${node.id}, 표시명: ${node.displayName}")
            }
            nodes.firstOrNull()?.let { node ->
                // 모바일 앱에 메시지 전송
                messageClient.sendMessage(node.id, "/request_health_data", byteArrayOf()).await()
            }
        } catch (e: Exception) {
            // 오류 처리
            Log.e(TAG, "헬스 데이터 요청 실패", e)
        }
    }
    
    // 물 섭취량 업데이트 (스마트폰으로 전송)
    suspend fun updateWaterIntake(ml: Int) {
        if (TEST_MODE) {
            // 테스트 모드: 로컬에서만 값 변경
            Log.d(TAG, "테스트 모드: 물 섭취량 업데이트 $ml ml")
            val currentData = _healthData.value
            val updatedData = currentData.copy(water = ml)
            _healthData.value = updatedData
            saveHealthDataToPrefs(updatedData)
            return
        }
        
        try {
            val data = JSONObject().apply {
                put("amount", ml.toDouble())
                put("dateTime", iso8601Format.format(Date()))
            }
            
            val nodes = nodeClient.connectedNodes.await()
            nodes.firstOrNull()?.let { node ->
                messageClient.sendMessage(
                    node.id,
                    "/update_water_intake",
                    data.toString().toByteArray()
                ).await()
            }
        } catch (e: Exception) {
            // 오류 처리
            Log.e(TAG, "물 섭취량 업데이트 실패", e)
        }
    }
    
    // 카페인 섭취량 업데이트 (스마트폰으로 전송)
    suspend fun updateCaffeineIntake(mg: Int) {
        if (TEST_MODE) {
            // 테스트 모드: 로컬에서만 값 변경
            Log.d(TAG, "테스트 모드: 카페인 섭취량 업데이트 $mg mg")
            val currentData = _healthData.value
            val updatedData = currentData.copy(caffeine = mg)
            _healthData.value = updatedData
            saveHealthDataToPrefs(updatedData)
            return
        }
        
        try {
            val data = JSONObject().apply {
                put("amount", mg.toDouble())
                put("dateTime", iso8601Format.format(Date()))
            }
            
            val nodes = nodeClient.connectedNodes.await()
            nodes.firstOrNull()?.let { node ->
                messageClient.sendMessage(
                    node.id,
                    "/update_caffeine_intake",
                    data.toString().toByteArray()
                ).await()
            }
        } catch (e: Exception) {
            // 오류 처리
            Log.e(TAG, "카페인 섭취량 업데이트 실패", e)
        }
    }
    
    // 메시지 수신 처리
    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "### 워치 앱: 메시지 수신: ${messageEvent.path}")
        when (messageEvent.path) {
            "/health_data_response" -> {
                val jsonString = String(messageEvent.data)
                try {
                    val jsonObject = JSONObject(jsonString)
                    
                    val calories = jsonObject.optDouble("calories", 0.0).toInt()
                    val steps = jsonObject.optInt("steps", 0)
                    val water = jsonObject.optDouble("water", 0.0).toInt()
                    val caffeine = jsonObject.optDouble("caffeine", 0.0).toInt()
                    
                    val stepsGoal = jsonObject.optInt("steps_goal", 10000)
                    val caloriesGoal = jsonObject.optInt("calories_goal", 2000)
                    val waterGoal = jsonObject.optInt("water_goal", 2000)
                    val caffeineGoal = jsonObject.optInt("caffeine_goal", 400)
                    
                    val updatedData = HealthData(
                        steps, calories, water, caffeine,
                        stepsGoal, caloriesGoal, waterGoal, caffeineGoal
                    )
                    
                    _healthData.value = updatedData
                    saveHealthDataToPrefs(updatedData)
                    
                } catch (e: Exception) {
                    Log.e(TAG, "헬스 데이터 파싱 실패", e)
                }
            }
            "/update_water_intake_result", "/update_caffeine_intake_result" -> {
                // 업데이트 결과 처리 (필요한 경우)
                val jsonString = String(messageEvent.data)
                try {
                    val jsonObject = JSONObject(jsonString)
                    val success = jsonObject.optBoolean("success", false)
                    
                    if (success) {
                        // 성공 시 헬스 데이터 다시 요청 (코루틴 스코프 내에서 호출)
                        scope.launch {
                            requestHealthData()
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "업데이트 결과 파싱 실패", e)
                }
            }
        }
    }
    
    // 데이터 동기화가 필요한 경우 이 메서드 구현
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        // 현재는 메시지 통신만 사용하므로 구현하지 않음
    }
} 