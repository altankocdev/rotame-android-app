package com.altankoc.rotame.feature.profile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val username: String? = null
)