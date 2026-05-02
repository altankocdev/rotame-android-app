package com.altankoc.rotame.feature.map.presentation.state

import com.altankoc.rotame.feature.location.domain.model.Location

sealed class MapUiState {
    data object Idle : MapUiState()
    data object Loading : MapUiState()
    data class Success(val locations: List<Location>) : MapUiState()
    data class Error(val message: String) : MapUiState()
}
