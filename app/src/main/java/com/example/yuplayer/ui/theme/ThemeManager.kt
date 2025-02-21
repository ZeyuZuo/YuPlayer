package com.example.yuplayer.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme")

class ThemeManager(private val context: Context) {
    private val isDarkModeKey = booleanPreferencesKey("is_dark_mode")

    val isDarkMode: Flow<Boolean> = context.themeDataStore.data
        .map { preferences ->
            preferences[isDarkModeKey] ?: false
        }

    suspend fun setDarkMode(isDark: Boolean) {
        context.themeDataStore.edit { preferences ->
            preferences[isDarkModeKey] = isDark
        }
    }
} 