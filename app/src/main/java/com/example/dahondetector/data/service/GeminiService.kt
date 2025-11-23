package com.example.dahondetector.data.service

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.dahondetector.data.model.DetectionResult
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class GeminiService(private val apiKey: String) {

    private val model by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey
        )
    }

    /**
     * Detects and identifies a plant from an image
     */
    suspend fun detectPlant(imagePath: String): Result<DetectionResult> {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = BitmapFactory.decodeFile(imagePath)
                    ?: return@withContext Result.failure(Exception("Failed to decode image"))

                val prompt = """
                    Analyze this plant image and provide the following information in a structured format:
                    
                    1. Plant Name: (Common name and scientific name if possible)
                    2. Confidence Level: (Your confidence in the identification as a percentage)
                    3. Description: (Detailed description of the plant, including its characteristics, habitat, and uses)
                    4. Care Instructions: (Basic care requirements if it's a cultivated plant)
                    
                    Please be specific and detailed in your response. Format your response as:
                    PLANT_NAME: [name]
                    CONFIDENCE: [percentage without % symbol]
                    DESCRIPTION: [detailed description]
                """.trimIndent()

                val response = model.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )

                val responseText = response.text ?: return@withContext Result.failure(
                    Exception("No response from AI")
                )

                val detectionResult = parseDetectionResponse(responseText, imagePath)
                Result.success(detectionResult)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Asks a question about a detected plant
     */
    suspend fun askAboutPlant(
        plantName: String,
        plantDescription: String,
        question: String
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    I have a plant identified as: $plantName
                    Description: $plantDescription
                    
                    Question: $question
                    
                    Please provide a detailed and helpful answer about this plant.
                """.trimIndent()

                val response = model.generateContent(prompt)
                val responseText = response.text ?: return@withContext Result.failure(
                    Exception("No response from AI")
                )

                Result.success(responseText)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * General chat about plants with image context
     */
    suspend fun chatWithImage(imagePath: String, message: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = BitmapFactory.decodeFile(imagePath)
                    ?: return@withContext Result.failure(Exception("Failed to decode image"))

                val response = model.generateContent(
                    content {
                        image(bitmap)
                        text(message)
                    }
                )

                val responseText = response.text ?: return@withContext Result.failure(
                    Exception("No response from AI")
                )

                Result.success(responseText)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun parseDetectionResponse(responseText: String, imagePath: String): DetectionResult {
        val lines = responseText.lines()
        var plantName = "Unknown Plant"
        var confidence = 0.8f
        var description = responseText

        for (line in lines) {
            when {
                line.startsWith("PLANT_NAME:", ignoreCase = true) -> {
                    plantName = line.substringAfter(":").trim()
                }
                line.startsWith("CONFIDENCE:", ignoreCase = true) -> {
                    val confidenceStr = line.substringAfter(":").trim()
                    confidence = try {
                        confidenceStr.toFloat() / 100f
                    } catch (e: Exception) {
                        0.8f
                    }
                }
                line.startsWith("DESCRIPTION:", ignoreCase = true) -> {
                    description = line.substringAfter(":").trim()
                }
            }
        }

        // If parsing failed, use the whole response as description
        if (plantName == "Unknown Plant" && description == responseText) {
            // Try to extract the first meaningful line as plant name
            val firstLine = lines.firstOrNull { it.isNotBlank() && !it.startsWith("PLANT_NAME") }
            if (firstLine != null && firstLine.length < 100) {
                plantName = firstLine.trim()
            }
        }

        return DetectionResult(
            id = System.currentTimeMillis().toString(),
            plantName = plantName,
            confidence = confidence,
            description = description,
            imageUri = imagePath,
            timestamp = System.currentTimeMillis(),
            location = ""
        )
    }
}

