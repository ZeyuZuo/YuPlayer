package com.example.yuplayer.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.yuplayer.model.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "servers")

class ServerRepository(private val context: Context) {
    private val serversKey = stringPreferencesKey("servers")
    private val json = Json { 
        ignoreUnknownKeys = true 
        classDiscriminator = "type"
    }

    suspend fun saveServer(server: Server) {
        try {
            val currentServers = getServers().toMutableList()
            currentServers.add(server)
            context.dataStore.edit { preferences ->
                preferences[serversKey] = json.encodeToString(currentServers)
            }
            Log.d("ServerRepository", "Server saved successfully: ${server.name}")
        } catch (e: Exception) {
            Log.e("ServerRepository", "Error saving server", e)
        }
    }

    suspend fun deleteServer(server: Server) {
        try {
            val currentServers = getServers().toMutableList()
            currentServers.remove(server)
            context.dataStore.edit { preferences ->
                preferences[serversKey] = json.encodeToString(currentServers)
            }
            Log.d("ServerRepository", "Server deleted successfully: ${server.name}")
        } catch (e: Exception) {
            Log.e("ServerRepository", "Error deleting server", e)
        }
    }

    fun getServersFlow(): Flow<List<Server>> {
        return context.dataStore.data
            .catch { exception ->
                Log.e("ServerRepository", "Error reading servers", exception)
                emit(emptyPreferences())
            }
            .map { preferences ->
                try {
                    val serversJson = preferences[serversKey] ?: "[]"
                    json.decodeFromString<List<Server>>(serversJson)
                } catch (e: Exception) {
                    Log.e("ServerRepository", "Error parsing servers", e)
                    emptyList()
                }
            }
    }

    private suspend fun getServers(): List<Server> {
        return try {
            val preferences = context.dataStore.data.first()
            val serversJson = preferences[serversKey] ?: "[]"
            json.decodeFromString(serversJson)
        } catch (e: Exception) {
            Log.e("ServerRepository", "Error getting servers", e)
            emptyList()
        }
    }
} 