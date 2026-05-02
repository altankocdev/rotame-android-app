package com.altankoc.rotame.feature.location.presentation.state

import com.altankoc.rotame.feature.location.domain.model.Location

sealed class LocationListUiState {
    data object Idle : LocationListUiState()
    data object Loading : LocationListUiState()
    data class Success(val locations: List<Location>) : LocationListUiState()
    data class Error(val message: String) : LocationListUiState()
}