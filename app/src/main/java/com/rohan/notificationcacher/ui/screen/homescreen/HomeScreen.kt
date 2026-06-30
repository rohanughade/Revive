package com.rohan.notificationcacher.ui.screen.homescreen

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.rohan.notificationcacher.ui.screen.messagescreen.SelectTopBar
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
    var selection by remember { mutableStateOf(false) }
    val selectedList by viewModel.selectedList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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
    BackHandler(true) {
        if (selection){
            selection = false
            viewModel.clearSelection()
        }else if(isSearchActive){
            isSearchActive = false
            searchText = ""
        }else{
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {

            if (isSearchActive) {
                SearchAppBar(
                    searchText = searchText,
                    onSearchTextChange = { searchText = it },
                    onCloseClick = { isSearchActive = false
                                   searchText = ""},
                    focusRequester = focusRequester
                )
            }else if (selection){
                SelectTopBar(
                    selectCount = selectedList.size,
                    onCancel = {selection = false
                               viewModel.clearSelection()},
                    onDelete = { viewModel.deleteByUser()}
                )
            } else {
                DefaultTopBar(onSearchClick = { isSearchActive = true }
                )
            }
        }


    ) { padding ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)){
            if (!isLoading && filteredUsers.isEmpty()){
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No users found", color = Color.Gray, fontWeight = FontWeight.Medium)
                }
            }else{
                LazyColumn (){
                  items(filteredUsers, key = {it}){user->
                      Item(
                          user = user,
                          onNavigate = {user,color->
                              navController.navigate("message/${Uri.encode(user)}/${color.toArgb()}") },
                          onItemSelect = {if (selection)viewModel.toggleSelection(user = user)},
                          onLongSelect = { selection = true
                                         viewModel.toggleSelection(user)},
                          selectionMode = selection,
                          isSelected = selectedList.contains(user)
                      )
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
fun Item(
    user: String,
    onNavigate: (String, Color) -> Unit,
    onItemSelect: (String) -> Unit,
    onLongSelect: (String) -> Unit,
    selectionMode: Boolean,
    isSelected: Boolean
) {
    val color = remember(user) { randomColor() }

    val backgroundColor by animateColorAsState(
        targetValue = if (selectionMode && isSelected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        } else {
            Color.Transparent
        },
        label = "itemBackground"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .combinedClickable(
                onClick = {
                    if (selectionMode) onItemSelect(user) else onNavigate(user, color)
                },
                onLongClick = { onLongSelect(user) }
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(targetState = selectionMode && isSelected, label = "avatarState") { selected ->
                if (selected) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = user.firstOrNull()?.uppercase() ?: "?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = user,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}








