package com.lixoten.datastorepreferences.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lixoten.datastorepreferences.data.UserPreferencesKeys.AGE
import com.lixoten.datastorepreferences.data.UserPreferencesKeys.FIRST_NAME
import com.lixoten.datastorepreferences.data.UserPreferencesKeys.LAST_NAME
import com.lixoten.datastorepreferences.data.UserPreferencesKeys.TOGGLE_ICON
import kotlinx.coroutines.flow.*
import java.io.IOException

// Required
private val Context.dataStore by preferencesDataStore("user_preferences")

// Define your data class
data class UserPreferences(
    val firstName: String = "",
    val lastName: String = "",
    val age: Int = 0,
    val toggleIcon: Boolean = false,
)

// Define your preference keys
object UserPreferencesKeys {
    val FIRST_NAME = stringPreferencesKey("first_name")
    val LAST_NAME = stringPreferencesKey("last_name")
    val AGE = intPreferencesKey("age")
    val TOGGLE_ICON = booleanPreferencesKey("toggle_icon")
}

// Define a DataStore class to store and retrieve your data
class UserPreferencesRepository(private val context: Context) {
    private val dataStore = context.dataStore

    // Update the user preferences
    suspend fun updateUserPreferences(
        firstName: String,
        lastName: String,
        age: Int,
        //toggleIcon: Boolean,
    ) {
        dataStore.edit { preferences ->
            preferences[FIRST_NAME] = firstName
            preferences[LAST_NAME] = lastName
            preferences[AGE] = age
            //preferences[TOGGLE_ICON] = toggleIcon
        }
    }

    // Update the user preferences
    suspend fun updateUserPreferencesIcon(
        toggleIcon: Boolean,
    ) {
        dataStore.edit { preferences ->
            preferences[TOGGLE_ICON] = toggleIcon
        }
    }

    // Read the user preferences as a Flow
    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                // Handle the exception
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                firstName = preferences[FIRST_NAME] ?: "",
                lastName = preferences[LAST_NAME] ?: "",
                age = preferences[AGE] ?: 0,
                toggleIcon = preferences[TOGGLE_ICON] ?: false,
            )
        }
}