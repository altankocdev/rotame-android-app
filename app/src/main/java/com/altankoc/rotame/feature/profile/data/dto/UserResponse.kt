package com.altankoc.rotame.feature.profile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val role: String,
    val authProvider: String,
    val createdAt: String
)