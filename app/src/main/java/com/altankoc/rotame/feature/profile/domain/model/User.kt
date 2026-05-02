package com.altankoc.rotame.feature.profile.domain.model

data class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val role: String,
    val authProvider: String,
    val createdAt: String
) {
    val fullName get() = "$firstName $lastName"
}