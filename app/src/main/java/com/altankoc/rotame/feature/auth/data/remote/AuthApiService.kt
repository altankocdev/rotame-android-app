package com.altankoc.rotame.feature.auth.data.remote

import com.altankoc.rotame.feature.auth.data.dto.AuthResponse
import com.altankoc.rotame.feature.auth.data.dto.LoginRequest
import com.altankoc.rotame.feature.auth.data.dto.RefreshTokenRequest
import com.altankoc.rotame.feature.auth.data.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/v1/auth/refresh")
    suspend fun refresh(@Body refreshToken: String): AuthResponse

    @POST("api/v1/auth/logout")
    suspend fun logout(@Body refreshToken: String)
}