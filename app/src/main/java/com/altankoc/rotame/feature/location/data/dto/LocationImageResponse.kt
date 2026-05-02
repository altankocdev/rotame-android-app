package com.altankoc.rotame.feature.location.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LocationImageResponse(
    val id: Long,
    val imageUrl: String,
    val cover: Boolean,
    val displayOrder: Int
)