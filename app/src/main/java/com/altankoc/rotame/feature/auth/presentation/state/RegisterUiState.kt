package com.altankoc.rotame.feature.auth.presentation.state

sealed class RegisterUiState {
    data object Idle : RegisterUiState()
    data object Loading : RegisterUiState()
    data object Success : RegisterUiState()
    data class SuccessWithMessage(val message: String) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}