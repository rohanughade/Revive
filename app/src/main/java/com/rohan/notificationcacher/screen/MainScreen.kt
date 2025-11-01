package com.rohan.notificationcacher.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rohan.notificationcacher.screen.homescreen.HomeScreen
import com.rohan.notificationcacher.screen.messagescreen.MessageScreen

@Composable
fun MainScreen() {
    Surface (modifier = Modifier.fillMaxSize()){
        val navController = rememberNavController()
        NavHost(navController, startDestination = "home"){
           composable("home"){
               HomeScreen(navController)
           }
            composable("message/{user}", arguments = listOf(
                navArgument("user"){type = NavType.StringType}
            )){
                val user = it.arguments?.getString("user")?:""
                MessageScreen(navController,user) {
                    navController.popBackStack()
                }
            }
        }
    }

}