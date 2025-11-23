package com.example.dahondetector.data.repository

import com.example.dahondetector.data.model.ChatLog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ChatLogRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val chatLogsCollection = firestore.collection("chatLogs")

    suspend fun saveChatLog(chatLog: ChatLog): Result<String> {
        return try {
            val documentRef = if (chatLog.id.isEmpty()) {
                chatLogsCollection.document()
            } else {
                chatLogsCollection.document(chatLog.id)
            }
            
            val chatLogToSave = chatLog.copy(id = documentRef.id)
            documentRef.set(chatLogToSave).await()
            Result.success(documentRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getChatLogs(): Flow<List<ChatLog>> = callbackFlow {
        val subscription = chatLogsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val chatLogs = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(ChatLog::class.java)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(chatLogs)
            }

        awaitClose { subscription.remove() }
    }

    suspend fun getChatLogById(id: String): Result<ChatLog?> {
        return try {
            val document = chatLogsCollection.document(id).get().await()
            val chatLog = document.toObject(ChatLog::class.java)
            Result.success(chatLog)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteChatLog(id: String): Result<Unit> {
        return try {
            chatLogsCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllChatLogs(): Result<Unit> {
        return try {
            val snapshot = chatLogsCollection.get().await()
            snapshot.documents.forEach { doc ->
                doc.reference.delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

