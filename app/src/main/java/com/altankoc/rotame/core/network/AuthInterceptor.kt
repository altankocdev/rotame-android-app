package com.altankoc.rotame.core.network

import com.altankoc.rotame.core.datastore.TokenDataStore
import com.altankoc.rotame.feature.auth.data.dto.AuthResponse
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val authEventBus: AuthEventBus
) : Interceptor {

    companion object {
        private const val BASE_URL     = "http://10.0.2.2:8080/"
        private const val REFRESH_PATH = "api/v1/auth/refresh"
        private const val LOGOUT_PATH  = "api/v1/auth/logout"
    }

    private val gson = Gson()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath.trimStart('/')

        if (path == REFRESH_PATH || path == LOGOUT_PATH) {
            return chain.proceed(originalRequest)
        }

        val accessToken = runBlocking { tokenDataStore.getAccessTokenOnce() }
        val response = chain.proceed(originalRequest.withBearer(accessToken))

        if (response.code == 401 || response.code == 403) {
            response.close()
            val newTokens = runBlocking { tryRefresh() }
            if (newTokens != null) {
                return chain.proceed(originalRequest.withBearer(newTokens.first))
            } else {
                runBlocking { tokenDataStore.clear() }
                authEventBus.sendEvent(AuthEvent.Unauthorized)
            }
        }

        return response
    }

    private suspend fun tryRefresh(): Pair<String, String>? {
        val refreshToken = tokenDataStore.getRefreshTokenOnce() ?: return null

        return try {
            val body = refreshToken.toRequestBody("text/plain".toMediaType())

            val request = Request.Builder()
                .url("$BASE_URL$REFRESH_PATH")
                .post(body)
                .build()

            val response = OkHttpClient().newCall(request).execute()

            android.util.Log.d("AuthInterceptor", "Refresh response code: ${response.code}")

            if (!response.isSuccessful) return null

            val responseBody = response.body?.string() ?: return null

            android.util.Log.d("AuthInterceptor", "Refresh response body: $responseBody")

            val authResponse = gson.fromJson(responseBody, AuthResponse::class.java)

            tokenDataStore.updateTokens(authResponse.accessToken, authResponse.refreshToken)
            Pair(authResponse.accessToken, authResponse.refreshToken)
        } catch (e: Exception) {
            android.util.Log.e("AuthInterceptor", "Refresh failed: ${e.message}", e)
            null
        }
    }

    private fun Request.withBearer(token: String?): Request =
        if (token.isNullOrBlank()) this
        else newBuilder().header("Authorization", "Bearer $token").build()
}