package com.rohan.notificationcacher.screen.homescreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.notificationcacher.repositery.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: MessageRepository) : ViewModel() {


   private var _users = MutableStateFlow<List<String>>(emptyList())
    val users: StateFlow<List<String>> = _users

    private var _selectedList = MutableStateFlow<Set<String>>(emptySet())
    val selectedList: StateFlow<Set<String>> = _selectedList


    private var _isLoading =MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
      getusers()
        initiliseApp()

    }

    private fun initiliseApp(){
        viewModelScope.launch {
            try {
                repository.getAllUser().first()
                delay(1500)
                _isLoading.value = false
            }catch (e: Exception){
                delay(1500)
                _isLoading.value = false
            }


        }
    }

   private fun getusers(){
        viewModelScope.launch {
            try {
                repository.getAllUser().collect {
                    _users.value = it

                }
            }catch (e: Exception){
                e.printStackTrace()

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