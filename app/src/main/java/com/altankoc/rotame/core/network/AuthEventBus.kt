package com.altankoc.rotame.core.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun sendEvent(event: AuthEvent) {
        _events.tryEmit(event)
    }
}

sealed class AuthEvent {
    data object Unauthorized : AuthEvent()
}