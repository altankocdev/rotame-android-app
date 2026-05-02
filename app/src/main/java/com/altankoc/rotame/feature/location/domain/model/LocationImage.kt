package com.altankoc.rotame.feature.location.domain.model

data class LocationImage(
    val id: Long,
    val imageUrl: String,
    val cover: Boolean,
    val displayOrder: Int
)