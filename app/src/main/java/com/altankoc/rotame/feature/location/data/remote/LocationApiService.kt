package com.altankoc.rotame.feature.location.data.remote

import com.altankoc.rotame.feature.location.data.dto.CreateLocationRequest
import com.altankoc.rotame.feature.location.data.dto.LocationImageResponse
import com.altankoc.rotame.feature.location.data.dto.LocationResponse
import com.altankoc.rotame.feature.location.data.dto.PagedResponse
import com.altankoc.rotame.feature.location.data.dto.UpdateLocationRequest
import okhttp3.MultipartBody
import retrofit2.http.*

interface LocationApiService {

    @POST("api/v1/locations")
    suspend fun createLocation(@Body request: CreateLocationRequest): LocationResponse

    @GET("api/v1/locations")
    suspend fun getLocations(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("onlyFavorites") onlyFavorites: Boolean = false
    ): PagedResponse<LocationResponse>

    @GET("api/v1/locations/{id}")
    suspend fun getLocation(@Path("id") id: Long): LocationResponse

    @PUT("api/v1/locations/{id}")
    suspend fun updateLocation(
        @Path("id") id: Long,
        @Body request: UpdateLocationRequest
    ): LocationResponse

    @DELETE("api/v1/locations/{id}")
    suspend fun deleteLocation(@Path("id") id: Long)

    @PATCH("api/v1/locations/{id}/favorite")
    suspend fun toggleFavorite(@Path("id") id: Long): LocationResponse

    @PATCH("api/v1/locations/{id}/restore")
    suspend fun restoreLocation(@Path("id") id: Long): LocationResponse

    @Multipart
    @POST("api/v1/locations/{id}/images")
    suspend fun uploadImage(
        @Path("id") locationId: Long,
        @Part file: MultipartBody.Part
    ): LocationImageResponse

    @GET("api/v1/locations/{id}/images")
    suspend fun getImages(@Path("id") locationId: Long): List<LocationImageResponse>

    @DELETE("api/v1/locations/{id}/images/{imageId}")
    suspend fun deleteImage(
        @Path("id") locationId: Long,
        @Path("imageId") imageId: Long
    )

    @PATCH("api/v1/locations/{id}/images/{imageId}/cover")
    suspend fun setCover(
        @Path("id") locationId: Long,
        @Path("imageId") imageId: Long
    ): LocationImageResponse
}