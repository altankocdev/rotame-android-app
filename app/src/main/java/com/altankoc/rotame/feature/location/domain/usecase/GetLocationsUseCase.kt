package com.altankoc.rotame.feature.location.domain.usecase

import com.altankoc.rotame.feature.location.domain.repository.LocationRepository
import javax.inject.Inject

class GetLocationsUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(page: Int = 0, size: Int = 20, onlyFavorites: Boolean = false) =
        locationRepository.getLocations(page, size, onlyFavorites)
}