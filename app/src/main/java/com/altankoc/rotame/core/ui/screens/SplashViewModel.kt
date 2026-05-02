package com.altankoc.rotame.core.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.rotame.core.datastore.TokenDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashUiState {
    data object Loading : SplashUiState()
    data object NavigateToLogin : SplashUiState()
    data object NavigateToMap : SplashUiState()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkAuth()
    }

    private fun checkAuth() {
        viewModelScope.launch {
            delay(3000)
            val token = tokenDataStore.accessToken.first()
            _uiState.value = if (token.isNullOrBlank()) {
                SplashUiState.NavigateToLogin
            } else {
                SplashUiState.NavigateToMap
            }
        }
    }
}