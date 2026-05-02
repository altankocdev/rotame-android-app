package com.altankoc.rotame.feature.location.domain.usecase

import com.altankoc.rotame.feature.location.domain.repository.LocationRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(id: Long) = locationRepository.toggleFavorite(id)
}