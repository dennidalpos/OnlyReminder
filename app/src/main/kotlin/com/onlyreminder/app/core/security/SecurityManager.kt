package com.onlyreminder.app.core.security

import com.onlyreminder.app.domain.security.SecurityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityManager @Inject constructor(
    private val repository: SecurityRepository,
) {
    private var lastActiveTime: Long = System.currentTimeMillis()

    private val _isLocked = MutableStateFlow(repository.isPinSet())
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    fun onAppForegrounded() {
        if (repository.isPinSet()) {
            val timeoutMinutes = repository.getAutoLockTimeout()
            if (timeoutMinutes == 0) {
                _isLocked.value = true
            } else {
                val elapsedMinutes = (System.currentTimeMillis() - lastActiveTime) / 60000
                if (elapsedMinutes >= timeoutMinutes) {
                    _isLocked.value = true
                }
            }
        }
    }

    fun onAppBackgrounded() {
        lastActiveTime = System.currentTimeMillis()
    }

    fun unlock() {
        _isLocked.value = false
    }
}
