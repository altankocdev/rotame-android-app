package com.altankoc.rotame.feature.auth.domain.model

data class AuthUser(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val role: String,
    val accessToken: String,
    val refreshToken: String
)