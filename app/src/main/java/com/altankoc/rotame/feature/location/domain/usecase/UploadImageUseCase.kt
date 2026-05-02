package com.altankoc.rotame.feature.location.domain.usecase

import com.altankoc.rotame.feature.location.domain.repository.LocationRepository
import java.io.File
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(locationId: Long, file: File) =
        locationRepository.uploadImage(locationId, file)
}