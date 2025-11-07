package com.rohan.notificationcacher.screen.messagescreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.rohan.notificationcacher.db.model.Message
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    navController: NavHostController,
    user: String, color: Color,
    onGoBack: () -> Unit
) {

    val viewModel: MessageViewModel = hiltViewModel()
    LaunchedEffect(user) {
        viewModel.getMessages(user)
    }

    val selectionList by viewModel.selectedMessages.collectAsState()
    var selectionMode by remember { mutableStateOf(false) }
    var color by remember {mutableStateOf(color) }

    BackHandler(enabled = true) {
        if (selectionMode){
            selectionMode = false
            viewModel.clearSelection()
        }else{
            onGoBack()
        }
    }
    val messages by viewModel.messages.collectAsState()
    Scaffold (
        topBar = {
            if (selectionMode){
                SelectTopBar(
                    selectCount = selectionList.size,
                    onCancel = {selectionMode = false
                               },
                    onDelete = {viewModel.deleteSelectedMessages()}
                )
            }else {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = {
                            onGoBack()
                            print("go back")
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(color = color)
                            ) {
                                Text(
                                    text = user[0].toString().uppercase(),
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            Text(
                                text = user,
                                fontSize = 15.sp,
                                style = MaterialTheme.typography.bodyMedium
                            )

                        }
                    }
                )
            }
        }
    ){padding->
        Box(Modifier
            .fillMaxSize()
            .padding(padding)
            .clipToBounds()){
            LazyColumn (reverseLayout = true){
                items(messages){
                    MessageBubble(
                        message = it,
                        selectionMode = selectionMode,
                        isSelected = selectionList.contains(it),
                        onSelect = {
                            if (selectionMode)viewModel.toggleSelection(it)
                        },
                        onLongSelect = {
                            selectionMode = true
                            viewModel.toggleSelection(it)
                        }
                    )
                }
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTopBar(
    selectCount: Int,
    onCancel:()-> Unit,
    onDelete:()-> Unit
) {
    TopAppBar(
        title = {Text("$selectCount", fontSize = 19.sp)},
        navigationIcon = {
            IconButton(onClick = {onCancel()}) {
                Icon(Icons.AutoMirrored.Default.ArrowBack,contentDescription = "back")
            }
        },
        actions = {IconButton(onClick = {onDelete()}) {
            Icon(Icons.Outlined.Delete, tint = Color.Red, contentDescription = "delete")
        }}

    )

}

@Composable
fun MessageBubble(message: Message,
                  isSelected: Boolean,
                  selectionMode: Boolean,
                  onSelect:(Message)-> Unit,
                  onLongSelect:(Message)-> Unit) {
    val dateFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { dateFormat.format(message.timestamp) }
    var imageModel = remember(message.imgUrl) {message.imgUrl }
    val offsetDp by animateDpAsState(
        targetValue = if (selectionMode) 40.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "offset"
    )


    Box(   modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp, 4.dp)
  ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()

        ) {
            AnimatedVisibility(
                visible = selectionMode,
                enter = fadeIn(animationSpec = tween(300)) + expandHorizontally(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)) + shrinkHorizontally(animationSpec = tween(300))
            ) {
                Box(
                    modifier = Modifier.width(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            }

            Box(
                modifier = Modifier
                    .offset(offsetDp)
                    .combinedClickable(
                        onClick = { onSelect(message) },
                        onLongClick = {
                            onLongSelect(message)
                        })
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Gray)

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(5.dp)
                ) {
                    message.senderName?.let {
                        Text(
                            text = message.senderName,
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(2.dp, 5.dp)
                        )
                    }
                    if (message.imgUrl != null) {
                        AsyncImage(
                            model = imageModel,
                            contentDescription = null,
                            modifier = Modifier.size(200.dp)
                        )
                        message.message.let {
                            Text(text = it, fontSize = 12.sp, color = Color.White)

                        }
                    } else {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(text = message.message, fontSize = 15.sp, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = formattedTime,
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier
                            .align(
                                Alignment.End
                            )
                            .padding(3.dp)
                    )
                }

            }
        }
    }







