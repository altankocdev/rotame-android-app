package com.altankoc.rotame.feature.location.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateLocationRequest(
    val name: String? = null,
    val description: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)