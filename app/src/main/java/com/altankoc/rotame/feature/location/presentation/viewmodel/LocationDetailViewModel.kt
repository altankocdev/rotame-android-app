package com.altankoc.rotame.feature.location.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.rotame.core.util.Resource
import com.altankoc.rotame.feature.location.domain.usecase.DeleteImageUseCase
import com.altankoc.rotame.feature.location.domain.usecase.DeleteLocationUseCase
import com.altankoc.rotame.feature.location.domain.usecase.GetLocationUseCase
import com.altankoc.rotame.feature.location.domain.usecase.SetCoverUseCase
import com.altankoc.rotame.feature.location.domain.usecase.ToggleFavoriteUseCase
import com.altankoc.rotame.feature.location.domain.usecase.UploadImageUseCase
import com.altankoc.rotame.feature.location.presentation.state.LocationDetailUiState
import com.altankoc.rotame.feature.location.presentation.state.AddEditLocationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class LocationDetailViewModel @Inject constructor(
    private val getLocationUseCase: GetLocationUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val deleteImageUseCase: DeleteImageUseCase,
    private val setCoverUseCase: SetCoverUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LocationDetailUiState>(LocationDetailUiState.Idle)
    val uiState: StateFlow<LocationDetailUiState> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow<AddEditLocationUiState>(AddEditLocationUiState.Idle)
    val actionState: StateFlow<AddEditLocationUiState> = _actionState.asStateFlow()

    fun getLocation(id: Long, showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = LocationDetailUiState.Loading
            }
            val result = getLocationUseCase(id)
            _uiState.value = when (result) {
                is Resource.Success -> LocationDetailUiState.Success(result.data)
                is Resource.Error -> LocationDetailUiState.Error(result.message)
                else -> LocationDetailUiState.Idle
            }
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            when (val result = toggleFavoriteUseCase(id)) {
                is Resource.Success -> getLocation(id, showLoading = false)
                is Resource.Error -> _actionState.value = AddEditLocationUiState.Error(result.message)
                else -> Unit
            }
        }
    }

    fun deleteLocation(id: Long) {
        viewModelScope.launch {
            _actionState.value = AddEditLocationUiState.Loading
            _actionState.value = when (val result = deleteLocationUseCase(id)) {
                is Resource.Success -> AddEditLocationUiState.Success
                is Resource.Error -> AddEditLocationUiState.Error(result.message)
                else -> AddEditLocationUiState.Idle
            }
        }
    }

    fun uploadImage(locationId: Long, file: File) {
        viewModelScope.launch {
            _actionState.value = AddEditLocationUiState.Loading
            when (val result = uploadImageUseCase(locationId, file)) {
                is Resource.Success -> getLocation(locationId)
                is Resource.Error -> _actionState.value = AddEditLocationUiState.Error(result.message)
                else -> Unit
            }
        }
    }

    fun deleteImage(locationId: Long, imageId: Long) {
        viewModelScope.launch {
            _actionState.value = AddEditLocationUiState.Loading
            when (val result = deleteImageUseCase(locationId, imageId)) {
                is Resource.Success -> getLocation(locationId)
                is Resource.Error -> _actionState.value = AddEditLocationUiState.Error(result.message)
                else -> Unit
            }
        }
    }

    fun setCover(locationId: Long, imageId: Long) {
        viewModelScope.launch {
            _actionState.value = AddEditLocationUiState.Loading
            when (val result = setCoverUseCase(locationId, imageId)) {
                is Resource.Success -> getLocation(locationId)
                is Resource.Error -> _actionState.value = AddEditLocationUiState.Error(result.message)
                else -> Unit
            }
        }
    }

    fun resetActionState() {
        _actionState.value = AddEditLocationUiState.Idle
    }
}