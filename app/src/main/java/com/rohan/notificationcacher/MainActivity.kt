package com.rohan.notificationcacher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.rohan.notificationcacher.screen.MainScreen
import com.rohan.notificationcacher.screen.homescreen.HomeViewModel
import com.rohan.notificationcacher.screen.splashcreen.SplashScreen
import com.rohan.notificationcacher.ui.theme.NotificationCacherTheme
import com.rohan.notificationcacher.util.isNotificationAccessGranted
import com.rohan.notificationcacher.util.requestPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotificationCacherTheme {
                val viewmodel: HomeViewModel = hiltViewModel()
                val isLoading by viewmodel.isLoading.collectAsState()
                val context = LocalContext.current
                var showDialog by remember { mutableStateOf(!isNotificationAccessGranted(context = context)) }


                if (isLoading) {
                    SplashScreen()

                } else {
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = {},
                            title = {
                                Text("Permission Required")
                            },
                            confirmButton = {
                                Button(onClick = {
                                    requestPermission(context)
                                    showDialog = false
                                }) {
                                    Text("Grant access")
                                }
                            },
                            dismissButton = {
                                Button(onClick = {
                                    showDialog = false
                                }) {
                                    Text("Cancel")
                                }
                            },
                            text = { Text("grant permission to access app") },

                            )
                    } else {
                        MainScreen()
                    }

                }
            }
        }
    }

}

