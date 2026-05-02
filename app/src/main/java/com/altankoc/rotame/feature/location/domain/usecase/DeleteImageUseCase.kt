package com.altankoc.rotame.feature.location.domain.usecase

import com.altankoc.rotame.feature.location.domain.repository.LocationRepository
import javax.inject.Inject

class DeleteImageUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(locationId: Long, imageId: Long) =
        locationRepository.deleteImage(locationId, imageId)
}