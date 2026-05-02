package com.altankoc.rotame.feature.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val role: String,
    val accessToken: String,
    val refreshToken: String
)