package com.example.sleeptight

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import org.json.JSONObject
import android.util.Log

class MainActivity: FlutterActivity() {
    private val WEAR_CHANNEL = "com.example.sleeptight/wear_os"
    private val TAG = "MainActivity"
    private lateinit var wearableService: WearableDataLayerService
    
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        
        // Wear OS 통신 서비스 초기화
        Log.d(TAG, "MainActivity: Wear OS 통신 서비스 초기화 시작")
        wearableService = WearableDataLayerService(context)
        wearableService.initialize()
        Log.d(TAG, "MainActivity: Wear OS 통신 서비스 초기화 완료")
        
        // 메서드 채널 설정
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, WEAR_CHANNEL).setMethodCallHandler { call, result ->
            Log.d(TAG, "메서드 채널 호출: ${call.method}")
            
            when (call.method) {
                "getConnectedNodes" -> {
                    Log.d(TAG, "getConnectedNodes 메서드 호출됨")
                    wearableService.getConnectedNodes(result)
                }
                
                "requestHealthData" -> {
                    Log.d(TAG, "requestHealthData 메서드 호출됨")
                    wearableService.requestHealthData(result)
                }
                
                "updateWaterIntake" -> {
                    val amount = call.argument<Double>("amount") ?: 0.0
                    Log.d(TAG, "updateWaterIntake 메서드 호출됨: $amount ml")
                    wearableService.updateWaterIntake(amount, result)
                }
                
                "updateCaffeineIntake" -> {
                    val amount = call.argument<Double>("amount") ?: 0.0
                    Log.d(TAG, "updateCaffeineIntake 메서드 호출됨: $amount mg")
                    wearableService.updateCaffeineIntake(amount, result)
                }
                
                "sendMessage" -> {
                    val nodeId = call.argument<String>("nodeId") ?: ""
                    val path = call.argument<String>("path") ?: ""
                    val message = call.argument<String>("message") ?: ""
                    
                    Log.d(TAG, "sendMessage 메서드 호출됨: nodeId=$nodeId, path=$path")
                    wearableService.sendMessage(nodeId, path, message.toByteArray(), result)
                }
                
                else -> {
                    Log.w(TAG, "지원하지 않는 메서드 호출: ${call.method}")
                    result.notImplemented()
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity: onResume()")
    }
    
    override fun onDestroy() {
        // 리소스 해제
        Log.d(TAG, "MainActivity: onDestroy()")
        wearableService.dispose()
        super.onDestroy()
    }
}
