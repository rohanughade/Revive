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


    private var _selectedMessages = MutableStateFlow<Set<Message>>(emptySet())
    val selectedMessages: StateFlow<Set<Message>> = _selectedMessages

    fun getMessages(sender: String){
        viewModelScope.launch {
            repository.getMessagesBySender(sender).collect {
                _messages.value =  it
            }

        }
    }

    fun toggleSelection(message: Message){
      val current=  _selectedMessages.value.toMutableSet()
            if (current.contains(message)) {
                current.remove(message)
            }else {
               current.add(message)
            }
        _selectedMessages.value = current
        }


    fun clearSelection(){
        _selectedMessages.value = emptySet()
    }

     fun deleteSelectedMessages(){
        viewModelScope.launch {
            _selectedMessages.value.forEach {
                repository.deleteMessage(it)
            }
            _selectedMessages.value = emptySet()
        }
    }


    }