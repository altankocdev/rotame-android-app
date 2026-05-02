package com.altankoc.rotame.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.rotame.core.util.Resource
import com.altankoc.rotame.feature.auth.domain.usecase.RegisterUseCase
import com.altankoc.rotame.feature.auth.presentation.state.RegisterUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        if (firstName.isBlank()) {
            _uiState.value = RegisterUiState.Error("Ad boş bırakılamaz")
            return
        }
        if (lastName.isBlank()) {
            _uiState.value = RegisterUiState.Error("Soyad boş bırakılamaz")
            return
        }
        if (username.isBlank()) {
            _uiState.value = RegisterUiState.Error("Kullanıcı adı boş bırakılamaz")
            return
        }
        if (email.isBlank()) {
            _uiState.value = RegisterUiState.Error("E-posta boş bırakılamaz")
            return
        }
        if (password.isBlank()) {
            _uiState.value = RegisterUiState.Error("Şifre boş bırakılamaz")
            return
        }
        if (password != confirmPassword) {
            _uiState.value = RegisterUiState.Error("Şifreler uyuşmuyor")
            return
        }
        if (password.length < 8) {
            _uiState.value = RegisterUiState.Error("Şifre en az 8 karakter olmalıdır")
            return
        }
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            _uiState.value = when (val result = registerUseCase(firstName, lastName, username, email, password)) {
                is Resource.Success -> RegisterUiState.SuccessWithMessage("Kayıt başarılı! Giriş yapabilirsiniz.")
                is Resource.Error -> RegisterUiState.Error(result.message)
                else -> RegisterUiState.Idle
            }
        }
    }


    fun resetState() {
        _uiState.value = RegisterUiState.Idle
    }
}