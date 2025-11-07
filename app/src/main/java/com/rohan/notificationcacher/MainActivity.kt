package com.rohan.notificationcacher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.rohan.notificationcacher.screen.MainScreen
import com.rohan.notificationcacher.screen.homescreen.HomeViewModel
import com.rohan.notificationcacher.screen.splashcreen.SplashScreen
import com.rohan.notificationcacher.ui.theme.NotificationCacherTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotificationCacherTheme {
                val viewmodel: HomeViewModel = hiltViewModel()
                val isLoading  by viewmodel.isLoading.collectAsState()


                if (isLoading){
                    SplashScreen()

                }else {
                    MainScreen()
                }
            }
        }
    }
}
