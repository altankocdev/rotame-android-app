package com.altankoc.rotame.feature.profile.presentation.state

sealed class ProfileActionUiState {
    data object Idle : ProfileActionUiState()
    data object Loading : ProfileActionUiState()
    data object Success : ProfileActionUiState()
    data class Error(val message: String) : ProfileActionUiState()
}