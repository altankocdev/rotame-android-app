package com.altankoc.rotame.feature.location.data.mapper

import com.altankoc.rotame.feature.location.data.dto.LocationImageResponse
import com.altankoc.rotame.feature.location.data.dto.LocationResponse
import com.altankoc.rotame.feature.location.domain.model.Location
import com.altankoc.rotame.feature.location.domain.model.LocationImage

fun LocationResponse.toDomain() = Location(
    id = id,
    name = name,
    description = description,
    latitude = latitude,
    longitude = longitude,
    favorite = favorite,
    images = images.map { it.toDomain() },
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun LocationImageResponse.toDomain() = LocationImage(
    id = id,
    imageUrl = imageUrl,
    cover = cover,
    displayOrder = displayOrder
)