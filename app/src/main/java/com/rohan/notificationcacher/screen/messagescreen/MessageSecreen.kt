package com.rohan.notificationcacher.screen.messagescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.rohan.notificationcacher.db.model.Message
import com.rohan.notificationcacher.util.randomColor
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(navController: NavHostController,user: String, onGoBack: () -> Unit) {

    val viewModel: MessageViewModel = hiltViewModel()
    LaunchedEffect(user) {
        viewModel.getMessages(user)
    }
    val messages by viewModel.messages.collectAsState()
    Scaffold (
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onGoBack()
                        print("go back")}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                    }
                },
                title={ Row {
                    Box(modifier = Modifier
                        .padding(12.dp)
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(color = randomColor())){
                        Text(text = user[0].toString().uppercase(), fontSize = 35.sp,color=Color.White,
                            textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Center))
                    }
                }
                    Text(text = user, fontSize = 20.sp, style = MaterialTheme.typography.bodyMedium)
                }
            )
        }
    ){padding->
        Box(Modifier
            .fillMaxSize()
            .padding(padding)){
            LazyColumn (reverseLayout = true){
                items(messages){
                    MessageBubble(it)
                }
            }
        }

    }
}


@Composable
fun MessageBubble(message: Message) {
    val dateFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { dateFormat.format(message.timestamp) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp, 6.dp)){
    Box(modifier = Modifier
        .padding(10.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(Color.Gray)
        .align(Alignment.CenterStart)){
        Column(modifier = Modifier.padding(5.dp)) {
            Text(text = message.sender, fontSize = 12.sp, color = Color.White, modifier = Modifier.padding(2.dp,5.dp))
            if (message.imgUrl!= null){
                AsyncImage(model = message.imgUrl, contentDescription = null, modifier = Modifier.size(200.dp))
                message.message.let {
                    Text(text = it, fontSize = 12.sp, color = Color.White)

                }
            }else{
                Text(text = message.message, fontSize = 15.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = formattedTime, fontSize = 12.sp, color = Color.White, modifier = Modifier.align(
                Alignment.End).padding(3.dp))
        }

    }
    }


}


