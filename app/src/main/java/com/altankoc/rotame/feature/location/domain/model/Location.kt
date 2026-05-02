package com.altankoc.rotame.feature.location.domain.model

data class Location(
    val id: Long,
    val name: String,
    val description: String?,
    val latitude: Double,
    val longitude: Double,
    val favorite: Boolean,
    val images: List<LocationImage>,
    val createdAt: String,
    val updatedAt: String
) {
    val coverImage get() = images.firstOrNull { it.cover } ?: images.firstOrNull()
}