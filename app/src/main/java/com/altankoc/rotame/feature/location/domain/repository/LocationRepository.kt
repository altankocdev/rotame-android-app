package com.altankoc.rotame.feature.location.domain.repository

import com.altankoc.rotame.core.util.Resource
import com.altankoc.rotame.feature.location.domain.model.Location
import com.altankoc.rotame.feature.location.domain.model.LocationImage
import java.io.File

interface LocationRepository {
    suspend fun createLocation(name: String, description: String?, latitude: Double, longitude: Double): Resource<Location>
    suspend fun getLocations(page: Int, size: Int, onlyFavorites: Boolean): Resource<List<Location>>
    suspend fun getLocation(id: Long): Resource<Location>
    suspend fun updateLocation(id: Long, name: String?, description: String?, latitude: Double?, longitude: Double?): Resource<Location>
    suspend fun deleteLocation(id: Long): Resource<Unit>
    suspend fun toggleFavorite(id: Long): Resource<Location>
    suspend fun restoreLocation(id: Long): Resource<Location>
    suspend fun uploadImage(locationId: Long, file: File): Resource<LocationImage>
    suspend fun getImages(locationId: Long): Resource<List<LocationImage>>
    suspend fun deleteImage(locationId: Long, imageId: Long): Resource<Unit>
    suspend fun setCover(locationId: Long, imageId: Long): Resource<LocationImage>
}