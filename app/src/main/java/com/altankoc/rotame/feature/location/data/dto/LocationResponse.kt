package com.altankoc.rotame.feature.location.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LocationResponse(
    val id: Long,
    val name: String,
    val description: String? = null,
    val latitude: Double,
    val longitude: Double,
    val favorite: Boolean,
    val images: List<LocationImageResponse> = emptyList(),
    val createdAt: String,
    val updatedAt: String
)