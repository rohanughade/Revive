package com.rohan.notificationcacher.screen.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.rohan.notificationcacher.util.randomColor
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val viewModel: HomeViewModel = hiltViewModel()
    val users by viewModel.users.collectAsState();
    var isSearchActive by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    val filteredUsers  = remember(users,searchText) {
        if (searchText.isBlank()) users
        else users.filter {
            it.contains(searchText, ignoreCase = true)
        }
    }


    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            delay(100)
            focusRequester.requestFocus()
        }
    }

    Scaffold(
        topBar = {

            if (isSearchActive) {
                SearchAppBar(
                    searchText = searchText,
                    onSearchTextChange = { searchText = it },
                    onCloseClick = { isSearchActive = false },
                    focusRequester = focusRequester
                )
            } else {
                DefaultTopBar(onSearchClick = { isSearchActive = true }
                )
            }
        }


    ) { padding ->

        Box(modifier = Modifier.fillMaxSize().padding(padding)){
            if (filteredUsers.isEmpty()){
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No users found", color = Color.Gray, fontWeight = FontWeight.Medium)
                }
            }else{
                LazyColumn (){
                  items(filteredUsers){user->
                      Item(user) {
                          navController.navigate("message/$it")
                      }
                  }
                }
            }
        }


    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopBar(onSearchClick:()-> Unit) {
    TopAppBar(
        title = {
            Text("Revive", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)

        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search,contentDescription = "search")
            }
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppBar(searchText: String,
                 onSearchTextChange:(String)-> Unit,
                 onCloseClick:()-> Unit,
                 focusRequester: FocusRequester) {
    TopAppBar(
        title = {
            TextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                placeholder = {
                    Text("Search...")
                },
                singleLine = true,
                leadingIcon = {
                    IconButton(onClick = onCloseClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp, 0.dp)
                    .focusRequester(focusRequester),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent

                )
            )
        }
    )
}

@Composable
fun Item(user: String,onItemSelect:(String)-> Unit) {
    val color = randomColor()
    Card (modifier = Modifier
        .fillMaxWidth()
        .padding(7.dp, 6.dp)
        .clickable(onClick = { onItemSelect(user) }),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(10.dp)
        ){
        Row (modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically){

            Box(modifier = Modifier
                .padding(12.dp)
                .size(50.dp)
                .clip(CircleShape)
                .background(color = color)){
                Text(text = user[0].toString().uppercase(), fontSize = 35.sp,color=Color.White,
                    textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Center))
            }
            Text(text = user, fontSize = 20.sp , fontWeight = FontWeight.Bold,modifier = Modifier.padding(8.dp))
        }
    }



}






