package com.altankoc.rotame.feature.location.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.rotame.core.util.Resource
import com.altankoc.rotame.feature.location.domain.model.LocationImage
import com.altankoc.rotame.feature.location.domain.usecase.CreateLocationUseCase
import com.altankoc.rotame.feature.location.domain.usecase.DeleteImageUseCase
import com.altankoc.rotame.feature.location.domain.usecase.GetLocationUseCase
import com.altankoc.rotame.feature.location.domain.usecase.SetCoverUseCase
import com.altankoc.rotame.feature.location.domain.usecase.UpdateLocationUseCase
import com.altankoc.rotame.feature.location.domain.usecase.UploadImageUseCase
import com.altankoc.rotame.feature.location.presentation.state.AddEditLocationUiState
import com.altankoc.rotame.feature.location.presentation.state.LocationDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddEditLocationViewModel @Inject constructor(
    private val createLocationUseCase: CreateLocationUseCase,
    private val updateLocationUseCase: UpdateLocationUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val deleteImageUseCase: DeleteImageUseCase,
    private val setCoverUseCase: SetCoverUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddEditLocationUiState>(AddEditLocationUiState.Idle)
    val uiState: StateFlow<AddEditLocationUiState> = _uiState.asStateFlow()

    private val _locationState = MutableStateFlow<LocationDetailUiState>(LocationDetailUiState.Idle)
    val locationState: StateFlow<LocationDetailUiState> = _locationState.asStateFlow()

    private val _lat = MutableStateFlow<Double?>(null)
    val lat: StateFlow<Double?> = _lat.asStateFlow()

    private val _lng = MutableStateFlow<Double?>(null)
    val lng: StateFlow<Double?> = _lng.asStateFlow()

    private val _pendingImages = MutableStateFlow<List<File>>(emptyList())
    val pendingImages: StateFlow<List<File>> = _pendingImages.asStateFlow()

    private val _existingImages = MutableStateFlow<List<LocationImage>>(emptyList())
    val existingImages: StateFlow<List<LocationImage>> = _existingImages.asStateFlow()

    private val _coverIndex = MutableStateFlow(0)
    val coverIndex: StateFlow<Int> = _coverIndex.asStateFlow()

    private val _savedLocationId = MutableStateFlow<Long?>(null)

    fun setInitialCoordinates(lat: Double?, lng: Double?) {
        _lat.value = lat
        _lng.value = lng
    }

    fun getLocation(id: Long) {
        viewModelScope.launch {
            _locationState.value = LocationDetailUiState.Loading
            _locationState.value = when (val result = getLocationUseCase(id)) {
                is Resource.Success -> {
                    _lat.value = result.data.latitude
                    _lng.value = result.data.longitude
                    _savedLocationId.value = result.data.id
                    _existingImages.value = result.data.images
                    LocationDetailUiState.Success(result.data)
                }
                is Resource.Error -> LocationDetailUiState.Error(result.message)
                else -> LocationDetailUiState.Idle
            }
        }
    }

    fun addPendingImage(file: File) {
        val totalImages = _existingImages.value.size + _pendingImages.value.size
        if (totalImages >= 4) return
        _pendingImages.value = _pendingImages.value + file
    }

    fun removePendingImage(index: Int) {
        val updated = _pendingImages.value.toMutableList()
        updated.removeAt(index)
        _pendingImages.value = updated
        if (_coverIndex.value >= updated.size && updated.isNotEmpty()) {
            _coverIndex.value = 0
        }
    }

    fun removeExistingImage(locationId: Long, imageId: Long) {
        viewModelScope.launch {
            when (deleteImageUseCase(locationId, imageId)) {
                is Resource.Success -> {
                    _existingImages.value = _existingImages.value.filter { it.id != imageId }
                }
                else -> Unit
            }
        }
    }

    fun setExistingCover(locationId: Long, imageId: Long) {
        viewModelScope.launch {
            when (setCoverUseCase(locationId, imageId)) {
                is Resource.Success -> {
                    _existingImages.value = _existingImages.value.map {
                        it.copy(cover = it.id == imageId)
                    }
                }
                else -> Unit
            }
        }
    }

    fun setCoverIndex(index: Int) {
        _coverIndex.value = index
    }

    fun updateCoordinates(lat: Double, lng: Double) {
        _lat.value = lat
        _lng.value = lng
    }

    fun createLocation(name: String, description: String?) {
        val latitude = _lat.value
        val longitude = _lng.value

        if (name.isBlank()) {
            _uiState.value = AddEditLocationUiState.Error("Konum adı boş bırakılamaz")
            return
        }
        if (latitude == null || longitude == null) {
            _uiState.value = AddEditLocationUiState.Error("Konum seçilmedi")
            return
        }

        viewModelScope.launch {
            _uiState.value = AddEditLocationUiState.Loading
            when (val result = createLocationUseCase(name, description, latitude, longitude)) {
                is Resource.Success -> {
                    val locationId = result.data.id
                    _savedLocationId.value = locationId
                    uploadPendingImages(locationId)
                }
                is Resource.Error -> _uiState.value = AddEditLocationUiState.Error(result.message)
                else -> Unit
            }
        }
    }

    fun updateLocation(id: Long, name: String?, description: String?) {
        viewModelScope.launch {
            _uiState.value = AddEditLocationUiState.Loading
            when (val result = updateLocationUseCase(id, name, description, _lat.value, _lng.value)) {
                is Resource.Success -> uploadPendingImages(id)
                is Resource.Error -> _uiState.value = AddEditLocationUiState.Error(result.message)
                else -> Unit
            }
        }
    }

    private suspend fun uploadPendingImages(locationId: Long) {
        val images = _pendingImages.value
        if (images.isEmpty()) {
            _uiState.value = AddEditLocationUiState.Success
            return
        }
        images.forEachIndexed { index, file ->
            when (val result = uploadImageUseCase(locationId, file)) {
                is Resource.Success -> {
                    if (index == _coverIndex.value) {
                        setCoverUseCase(locationId, result.data.id)
                    }
                }
                is Resource.Error -> {
                    _uiState.value = AddEditLocationUiState.Error(result.message)
                    return
                }
                else -> Unit
            }
        }
        _uiState.value = AddEditLocationUiState.Success
    }

    fun resetState() {
        _uiState.value = AddEditLocationUiState.Idle
    }
}