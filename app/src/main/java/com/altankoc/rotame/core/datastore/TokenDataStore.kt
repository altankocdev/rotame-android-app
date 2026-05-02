package com.altankoc.rotame.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "rotame_prefs")

@Singleton
class TokenDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_ACCESS_TOKEN  = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_USER_ID       = longPreferencesKey("user_id")
        private val KEY_USER_EMAIL    = stringPreferencesKey("user_email")
        private val KEY_FIRST_NAME    = stringPreferencesKey("first_name")
        private val KEY_LAST_NAME     = stringPreferencesKey("last_name")
        private val KEY_USERNAME      = stringPreferencesKey("username")
    }

    val accessToken: Flow<String?>  = context.dataStore.data.map { it[KEY_ACCESS_TOKEN] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[KEY_REFRESH_TOKEN] }
    val userId: Flow<Long?>         = context.dataStore.data.map { it[KEY_USER_ID] }
    val userEmail: Flow<String?>    = context.dataStore.data.map { it[KEY_USER_EMAIL] }
    val firstName: Flow<String?>    = context.dataStore.data.map { it[KEY_FIRST_NAME] }
    val lastName: Flow<String?>     = context.dataStore.data.map { it[KEY_LAST_NAME] }
    val username: Flow<String?>     = context.dataStore.data.map { it[KEY_USERNAME] }

    suspend fun saveAuthData(
        accessToken: String,
        refreshToken: String,
        userId: Long,
        email: String,
        firstName: String,
        lastName: String,
        username: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN]  = accessToken
            prefs[KEY_REFRESH_TOKEN] = refreshToken
            prefs[KEY_USER_ID]       = userId
            prefs[KEY_USER_EMAIL]    = email
            prefs[KEY_FIRST_NAME]    = firstName
            prefs[KEY_LAST_NAME]     = lastName
            prefs[KEY_USERNAME]      = username
        }
    }

    suspend fun updateTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN]  = accessToken
            prefs[KEY_REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun getAccessTokenOnce(): String? =
        context.dataStore.data.map { it[KEY_ACCESS_TOKEN] }.first()

    suspend fun getRefreshTokenOnce(): String? =
        context.dataStore.data.map { it[KEY_REFRESH_TOKEN] }.first()
}