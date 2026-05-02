package com.altankoc.rotame.feature.location.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateLocationRequest(
    val name: String,
    val description: String? = null,
    val latitude: Double,
    val longitude: Double
)