package com.altankoc.rotame.feature.location.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PagedResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val number: Int,
    val size: Int,
    val last: Boolean
)