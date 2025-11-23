package com.example.dahondetector.data.model

import com.google.firebase.firestore.PropertyName

data class ChatLog(
    @PropertyName("id") val id: String = "",
    @PropertyName("plantName") val plantName: String = "",
    @PropertyName("plantImageUri") val plantImageUri: String = "",
    @PropertyName("confidence") val confidence: Float = 0f,
    @PropertyName("userQuery") val userQuery: String = "",
    @PropertyName("aiResponse") val aiResponse: String = "",
    @PropertyName("timestamp") val timestamp: Long = System.currentTimeMillis()
) {
    // No-arg constructor for Firebase
    constructor() : this("", "", "", 0f, "", "", System.currentTimeMillis())
}
