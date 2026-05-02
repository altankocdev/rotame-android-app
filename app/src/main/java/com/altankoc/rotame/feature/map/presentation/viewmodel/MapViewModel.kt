package com.altankoc.rotame.feature.map.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.rotame.core.util.Resource
import com.altankoc.rotame.feature.location.domain.usecase.GetLocationsUseCase
import com.altankoc.rotame.feature.map.presentation.state.MapUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getLocationsUseCase: GetLocationsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Idle)
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        getLocations()
    }

    fun getLocations(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = MapUiState.Loading
            }
            val result = getLocationsUseCase()
            _uiState.value = when (result) {
                is Resource.Success -> MapUiState.Success(result.data)
                is Resource.Error -> MapUiState.Error(result.message)
                else -> MapUiState.Idle
            }
        }
    }

}