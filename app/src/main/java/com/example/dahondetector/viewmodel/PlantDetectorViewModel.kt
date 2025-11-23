package com.example.dahondetector.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dahondetector.data.local.ApiKeyManager
import com.example.dahondetector.data.model.ChatLog
import com.example.dahondetector.data.model.DetectionResult
import com.example.dahondetector.data.repository.ChatLogRepository
import com.example.dahondetector.data.service.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val message: String = "") : UiState()
    data class Error(val message: String) : UiState()
}

class PlantDetectorViewModel(context: Context) : ViewModel() {

    private val apiKeyManager = ApiKeyManager(context)
    private val chatLogRepository = ChatLogRepository()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _apiKey = MutableStateFlow<String?>(null)
    val apiKey: StateFlow<String?> = _apiKey.asStateFlow()

    private val _detectionResult = MutableStateFlow<DetectionResult?>(null)
    val detectionResult: StateFlow<DetectionResult?> = _detectionResult.asStateFlow()

    private val _chatLogs = MutableStateFlow<List<ChatLog>>(emptyList())
    val chatLogs: StateFlow<List<ChatLog>> = _chatLogs.asStateFlow()

    private val _currentChatResponse = MutableStateFlow<String>("")
    val currentChatResponse: StateFlow<String> = _currentChatResponse.asStateFlow()

    private var geminiService: GeminiService? = null

    init {
        loadApiKey()
        loadChatLogs()
    }

    private fun loadApiKey() {
        viewModelScope.launch {
            apiKeyManager.apiKey.collect { key ->
                _apiKey.value = key
                if (key != null) {
                    geminiService = GeminiService(key)
                }
            }
        }
    }

    fun saveApiKey(apiKey: String) {
        viewModelScope.launch {
            try {
                apiKeyManager.saveApiKey(apiKey)
                geminiService = GeminiService(apiKey)
                _uiState.value = UiState.Success("API Key saved successfully")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to save API key: ${e.message}")
            }
        }
    }

    fun detectPlant(imagePath: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val service = geminiService ?: run {
                    _uiState.value = UiState.Error("Please set up your Gemini API key first")
                    return@launch
                }

                val result = service.detectPlant(imagePath)
                result.fold(
                    onSuccess = { detection ->
                        _detectionResult.value = detection
                        _uiState.value = UiState.Success("Plant detected successfully")
                    },
                    onFailure = { error ->
                        _uiState.value = UiState.Error("Detection failed: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Detection failed: ${e.message}")
            }
        }
    }

    fun askAboutPlant(question: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val service = geminiService ?: run {
                    _uiState.value = UiState.Error("Please set up your Gemini API key first")
                    return@launch
                }

                val detection = _detectionResult.value ?: run {
                    _uiState.value = UiState.Error("No plant detected yet")
                    return@launch
                }

                val result = service.askAboutPlant(
                    plantName = detection.plantName,
                    plantDescription = detection.description,
                    question = question
                )

                result.fold(
                    onSuccess = { response ->
                        _currentChatResponse.value = response
                        
                        // Save chat log to Firebase
                        val chatLog = ChatLog(
                            plantName = detection.plantName,
                            plantImageUri = detection.imageUri,
                            confidence = detection.confidence,
                            userQuery = question,
                            aiResponse = response,
                            timestamp = System.currentTimeMillis()
                        )
                        saveChatLog(chatLog)
                        
                        _uiState.value = UiState.Success()
                    },
                    onFailure = { error ->
                        _uiState.value = UiState.Error("Failed to get response: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to get response: ${e.message}")
            }
        }
    }

    private fun saveChatLog(chatLog: ChatLog) {
        viewModelScope.launch {
            try {
                chatLogRepository.saveChatLog(chatLog)
            } catch (e: Exception) {
                // Silently fail, don't interrupt the user experience
            }
        }
    }

    private fun loadChatLogs() {
        viewModelScope.launch {
            try {
                chatLogRepository.getChatLogs().collect { logs ->
                    _chatLogs.value = logs
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to load chat logs: ${e.message}")
            }
        }
    }

    fun deleteChatLog(chatLogId: String) {
        viewModelScope.launch {
            try {
                chatLogRepository.deleteChatLog(chatLogId)
                _uiState.value = UiState.Success("Chat log deleted")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to delete: ${e.message}")
            }
        }
    }

    fun clearDetectionResult() {
        _detectionResult.value = null
        _currentChatResponse.value = ""
    }

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }
}

