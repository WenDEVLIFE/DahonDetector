package com.example.dahondetector.data.model

import com.google.firebase.firestore.PropertyName

data class DetectionResult(
    @PropertyName("id") val id: String = "",
    @PropertyName("plantName") val plantName: String = "",
    @PropertyName("confidence") val confidence: Float = 0f,
    @PropertyName("description") val description: String = "",
    @PropertyName("imageUri") val imageUri: String = "",
    @PropertyName("timestamp") val timestamp: Long = System.currentTimeMillis(),
    @PropertyName("location") val location: String = ""
) {
    // No-arg constructor for Firebase
    constructor() : this("", "", 0f, "", "", System.currentTimeMillis(), "")
}
