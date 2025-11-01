package com.rohan.notificationcacher.screen.homescreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.notificationcacher.repositery.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: MessageRepository) : ViewModel() {


   private var _users = MutableStateFlow<List<String>>(emptyList())
    val users: StateFlow<List<String>> = _users

    init {
      getusers()

    }

   private fun getusers(){
        viewModelScope.launch {
            repository.getAllUser().collect {
                _users.value = it
            }

        }
    }

}