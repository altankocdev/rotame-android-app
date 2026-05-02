package com.altankoc.rotame.feature.location.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.rotame.core.util.Resource
import com.altankoc.rotame.feature.location.domain.usecase.DeleteLocationUseCase
import com.altankoc.rotame.feature.location.domain.usecase.GetLocationsUseCase
import com.altankoc.rotame.feature.location.domain.usecase.ToggleFavoriteUseCase
import com.altankoc.rotame.feature.location.presentation.state.LocationListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationListViewModel @Inject constructor(
    private val getLocationsUseCase: GetLocationsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LocationListUiState>(LocationListUiState.Idle)
    val uiState: StateFlow<LocationListUiState> = _uiState.asStateFlow()

    private var onlyFavorites = false

    init {
        getLocations()
    }

    fun getLocations(onlyFavorites: Boolean = this.onlyFavorites, showLoading: Boolean = true) {
        this.onlyFavorites = onlyFavorites
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = LocationListUiState.Loading
            }
            val result = getLocationsUseCase(onlyFavorites = onlyFavorites)
            _uiState.value = when (result) {
                is Resource.Success -> LocationListUiState.Success(result.data)
                is Resource.Error -> LocationListUiState.Error(result.message)
                else -> LocationListUiState.Idle
            }
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            when (val result = toggleFavoriteUseCase(id)) {
                is Resource.Success -> getLocations(onlyFavorites = onlyFavorites, showLoading = false)
                is Resource.Error -> _uiState.value = LocationListUiState.Error(result.message)
                else -> Unit
            }
        }
    }

    fun deleteLocation(id: Long) {
        viewModelScope.launch {
            deleteLocationUseCase(id)
            getLocations(onlyFavorites)
        }
    }
}