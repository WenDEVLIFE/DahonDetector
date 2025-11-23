package com.example.dahondetector.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.dahondetector.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ApiKeyManager(private val context: Context) {
    private val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")

    /**
     * Get API key from DataStore, fallback to BuildConfig if not set
     * Priority: DataStore > BuildConfig (.env)
     */
    val apiKey: Flow<String?> = context.dataStore.data.map { preferences ->
        val storedKey = preferences[GEMINI_API_KEY]
        when {
            !storedKey.isNullOrEmpty() -> storedKey
            isEnvApiKeyValid() -> BuildConfig.GEMINI_API_KEY
            else -> null
        }
    }

    suspend fun saveApiKey(apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[GEMINI_API_KEY] = apiKey
        }
    }

    suspend fun getApiKey(): String? {
        var key: String? = null
        context.dataStore.data.map { preferences ->
            val storedKey = preferences[GEMINI_API_KEY]
            when {
                !storedKey.isNullOrEmpty() -> storedKey
                isEnvApiKeyValid() -> BuildConfig.GEMINI_API_KEY
                else -> null
            }
        }.collect { key = it }
        return key
    }

    suspend fun clearApiKey() {
        context.dataStore.edit { preferences ->
            preferences.remove(GEMINI_API_KEY)
        }
    }

    /**
     * Check if the BuildConfig API key is valid (not placeholder)
     */
    private fun isEnvApiKeyValid(): Boolean {
        val envKey = BuildConfig.GEMINI_API_KEY
        return !envKey.isNullOrEmpty() && envKey != "your_api_key_here"
    }

    /**
     * Check if API key is configured (either in DataStore or .env)
     */
    suspend fun isConfigured(): Boolean {
        return getApiKey() != null
    }
}

