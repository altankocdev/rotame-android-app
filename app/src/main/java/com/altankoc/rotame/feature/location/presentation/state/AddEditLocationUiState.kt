package com.altankoc.rotame.feature.location.presentation.state

sealed class AddEditLocationUiState {
    data object Idle : AddEditLocationUiState()
    data object Loading : AddEditLocationUiState()
    data object Success : AddEditLocationUiState()
    data class Error(val message: String) : AddEditLocationUiState()
}