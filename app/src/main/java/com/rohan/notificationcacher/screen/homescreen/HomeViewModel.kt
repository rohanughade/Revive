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

    private var _selectedList = MutableStateFlow<Set<String>>(emptySet())
    val selectedList: StateFlow<Set<String>> = _selectedList

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

    fun toggleSelection(user: String){
        val current = _selectedList.value.toMutableSet()
        if (current.contains(user)){
            current.remove(user)
        }else{
            current.add(user)
        }
        _selectedList.value = current
    }

    fun clearSelection(){
        _selectedList.value  = emptySet()
    }

    fun deleteByUser(){
        viewModelScope.launch {
            _selectedList.value.forEach {
                repository.deleteMessageByUser(it)
            }
            _selectedList.value = emptySet()
        }

    }

}