package com.onlyreminder.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.onlyreminder.app.core.navigation.Route
import com.onlyreminder.app.core.security.SecurityManager
import com.onlyreminder.app.domain.security.SecurityRepository
import com.onlyreminder.app.features.security.ui.LockScreen
import com.onlyreminder.app.ui.MainScreen
import com.onlyreminder.app.ui.MainViewModel
import com.onlyreminder.app.ui.theme.OnlyReminderTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var securityManager: SecurityManager

    @Inject
    lateinit var securityRepository: SecurityRepository

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    securityManager.onAppForegrounded()
                }

                override fun onStop(owner: LifecycleOwner) {
                    securityManager.onAppBackgrounded()
                }
            },
        )

        setContent {
            val isLocked by securityManager.isLocked.collectAsState()
            val onboardingCompleted by mainViewModel.onboardingCompleted.collectAsState()

            OnlyReminderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    if (isLocked) {
                        LockScreen(
                            repository = securityRepository,
                        ) {
                            securityManager.unlock()
                        }
                    } else {
                        val startDestination =
                            if (onboardingCompleted) Route.Home else Route.Onboarding
                        MainScreen(
                            startDestination = startDestination,
                        )
                    }
                }
            }
        }
    }
}
