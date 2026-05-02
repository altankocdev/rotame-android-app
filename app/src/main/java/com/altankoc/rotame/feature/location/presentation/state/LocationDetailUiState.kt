package com.altankoc.rotame.feature.location.presentation.state

import com.altankoc.rotame.feature.location.domain.model.Location

sealed class LocationDetailUiState {
    data object Idle : LocationDetailUiState()
    data object Loading : LocationDetailUiState()
    data class Success(val location: Location) : LocationDetailUiState()
    data class Error(val message: String) : LocationDetailUiState()
}