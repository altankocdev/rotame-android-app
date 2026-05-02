package com.altankoc.rotame.feature.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val identifier: String,
    val password: String
)