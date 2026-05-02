package com.altankoc.rotame.feature.location.data.repository

import com.altankoc.rotame.core.network.safeApiCall
import com.altankoc.rotame.core.util.Resource
import com.altankoc.rotame.feature.location.data.dto.CreateLocationRequest
import com.altankoc.rotame.feature.location.data.dto.UpdateLocationRequest
import com.altankoc.rotame.feature.location.data.mapper.toDomain
import com.altankoc.rotame.feature.location.data.remote.LocationApiService
import com.altankoc.rotame.feature.location.domain.model.Location
import com.altankoc.rotame.feature.location.domain.model.LocationImage
import com.altankoc.rotame.feature.location.domain.repository.LocationRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val locationApiService: LocationApiService
) : LocationRepository {

    override suspend fun createLocation(
        name: String,
        description: String?,
        latitude: Double,
        longitude: Double
    ): Resource<Location> = safeApiCall {
        locationApiService.createLocation(
            CreateLocationRequest(
                name = name,
                description = description,
                latitude = latitude,
                longitude = longitude
            )
        ).toDomain()
    }

    override suspend fun getLocations(
        page: Int,
        size: Int,
        onlyFavorites: Boolean
    ): Resource<List<Location>> = safeApiCall {
        locationApiService.getLocations(page, size, onlyFavorites).content.map { it.toDomain() }
    }

    override suspend fun getLocation(id: Long): Resource<Location> = safeApiCall {
        locationApiService.getLocation(id).toDomain()
    }

    override suspend fun updateLocation(
        id: Long,
        name: String?,
        description: String?,
        latitude: Double?,
        longitude: Double?
    ): Resource<Location> = safeApiCall {
        locationApiService.updateLocation(
            id,
            UpdateLocationRequest(
                name = name,
                description = description,
                latitude = latitude,
                longitude = longitude
            )
        ).toDomain()
    }

    override suspend fun deleteLocation(id: Long): Resource<Unit> = safeApiCall {
        locationApiService.deleteLocation(id)
    }

    override suspend fun toggleFavorite(id: Long): Resource<Location> = safeApiCall {
        locationApiService.toggleFavorite(id).toDomain()
    }

    override suspend fun restoreLocation(id: Long): Resource<Location> = safeApiCall {
        locationApiService.restoreLocation(id).toDomain()
    }

    override suspend fun uploadImage(locationId: Long, file: File): Resource<LocationImage> = safeApiCall {
        val mimeType = when (file.extension.lowercase()) {
            "png" -> "image/png"
            "webp" -> "image/webp"
            else -> "image/jpeg"
        }
        val requestBody = file.asRequestBody(mimeType.toMediaType())
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
        locationApiService.uploadImage(locationId, part).toDomain()
    }

    override suspend fun getImages(locationId: Long): Resource<List<LocationImage>> = safeApiCall {
        locationApiService.getImages(locationId).map { it.toDomain() }
    }

    override suspend fun deleteImage(locationId: Long, imageId: Long): Resource<Unit> = safeApiCall {
        locationApiService.deleteImage(locationId, imageId)
    }

    override suspend fun setCover(locationId: Long, imageId: Long): Resource<LocationImage> = safeApiCall {
        locationApiService.setCover(locationId, imageId).toDomain()
    }
}