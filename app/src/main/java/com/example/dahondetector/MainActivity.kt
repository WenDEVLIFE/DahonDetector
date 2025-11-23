package com.example.dahondetector

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.dahondetector.data.model.DetectionResult
import com.example.dahondetector.ui.screens.*
import com.example.dahondetector.ui.theme.DahonDetectorTheme
import com.example.dahondetector.viewmodel.PlantDetectorViewModel
import com.example.dahondetector.viewmodel.UiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DahonDetectorTheme {
                val viewModel = remember { PlantDetectorViewModel(this) }
                AppNavigation(viewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: PlantDetectorViewModel) {
    val context = LocalContext.current
    
    var currentScreen by remember { mutableStateOf("home") }
    val apiKey by viewModel.apiKey.collectAsState()
    val detectionResult by viewModel.detectionResult.collectAsState()
    val chatLogs by viewModel.chatLogs.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val chatResponse by viewModel.currentChatResponse.collectAsState()

    // Handle UI state changes (show toasts)
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is UiState.Success -> {
                if (state.message.isNotEmpty()) {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                viewModel.resetUiState()
            }
            is UiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetUiState()
            }
            else -> {}
        }
    }

    when (currentScreen) {
        "home" -> {
            HomeScreen(
                onCameraClick = {
                    if (apiKey != null) {
                        currentScreen = "camera"
                    } else {
                        currentScreen = "api_key"
                    }
                },
                onChatLogsClick = { currentScreen = "chat_logs" },
                onApiKeySetupClick = { currentScreen = "api_key" },
                hasApiKey = apiKey != null
            )
        }
        
        "api_key" -> {
            ApiKeyScreen(
                currentApiKey = apiKey,
                onSaveApiKey = { key ->
                    viewModel.saveApiKey(key)
                    currentScreen = "home"
                },
                onBackClick = { currentScreen = "home" }
            )
        }
        
        "camera" -> {
            CameraScreen(
                onBackClick = { currentScreen = "home" },
                onPhotoTaken = { imagePath ->
                    viewModel.detectPlant(imagePath)
                    currentScreen = "detecting"
                }
            )
        }
        
        "detecting" -> {
            // Show loading screen while detecting
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Analyzing plant...")
                }
            }
            
            // Navigate to result when detection completes
            LaunchedEffect(detectionResult) {
                if (detectionResult != null) {
                    currentScreen = "result"
                }
            }
        }
        
        "result" -> {
            detectionResult?.let { result ->
                DetectionResultScreen(
                    detectionResult = result,
                    onBackClick = {
                        viewModel.clearDetectionResult()
                        currentScreen = "home"
                    },
                    onAskQuestion = { question ->
                        viewModel.askAboutPlant(question)
                    },
                    chatResponse = chatResponse,
                    isLoading = uiState is UiState.Loading
                )
            }
        }
        
        "chat_logs" -> {
            ChatLogsScreen(
                chatLogs = chatLogs,
                onBackClick = { currentScreen = "home" },
                onItemClick = { chatLog ->
                    // Could navigate to a detail view
                    Toast.makeText(context, "Chat log: ${chatLog.plantName}", Toast.LENGTH_SHORT).show()
                },
                onDeleteClick = { id ->
                    viewModel.deleteChatLog(id)
                }
            )
        }
    }
}
