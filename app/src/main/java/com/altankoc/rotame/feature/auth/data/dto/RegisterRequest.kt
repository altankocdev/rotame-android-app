package com.altankoc.rotame.feature.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String
)