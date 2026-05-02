package com.altankoc.rotame.feature.location.domain.usecase

import com.altankoc.rotame.feature.location.domain.repository.LocationRepository
import javax.inject.Inject

class UpdateLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(
        id: Long,
        name: String?,
        description: String?,
        latitude: Double?,
        longitude: Double?
    ) = locationRepository.updateLocation(id, name, description, latitude, longitude)
}