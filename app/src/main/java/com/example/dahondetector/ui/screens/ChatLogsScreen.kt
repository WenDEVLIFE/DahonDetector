package com.example.dahondetector.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dahondetector.data.model.ChatLog
import com.example.dahondetector.ui.components.ChatLogItem
import com.example.dahondetector.ui.components.EmptyChatLogsPlaceholder
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatLogsScreen(
    chatLogs: List<ChatLog> = emptyList(),
    onBackClick: () -> Unit = {},
    onItemClick: (ChatLog) -> Unit = {},
    onDeleteClick: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chat Logs",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←", fontSize = 24.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        if (chatLogs.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EmptyChatLogsPlaceholder()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 8.dp)
            ) {
                items(chatLogs) { chatLog ->
                    ChatLogItem(
                        plantName = chatLog.plantName,
                        userQuery = chatLog.userQuery,
                        aiResponse = chatLog.aiResponse,
                        timestamp = chatLog.timestamp,
                        onClick = { onItemClick(chatLog) },
                        onDeleteClick = { onDeleteClick(chatLog.id) }
                    )
                }
            }
        }
    }
}

