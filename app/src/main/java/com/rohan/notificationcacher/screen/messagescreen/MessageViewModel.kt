package com.rohan.notificationcacher.screen.messagescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.notificationcacher.db.model.Message
import com.rohan.notificationcacher.repositery.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(private val repository: MessageRepository): ViewModel() {
    private var _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    fun getMessages(sender: String){
        viewModelScope.launch {
            repository.getMessagesBySender(sender).collect {
                _messages.value =  it
            }

        }
    }

    fun deleteMessage(message: Message){
        viewModelScope.launch {
            repository.deleteMessage(message)
            getMessages(message.sender)

        }

    }

}