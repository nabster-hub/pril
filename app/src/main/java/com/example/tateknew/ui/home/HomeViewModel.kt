package com.example.tateknew.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _tvWelcome = MutableLiveData<String>()
    val tvWelcome: LiveData<String> = _tvWelcome

    private val _tvNext = MutableLiveData<String>()
    val tvNext: LiveData<String> = _tvNext

    fun setWelcome(welcome: String) {
        _tvWelcome.value = welcome
    }

    fun setNext(next: String) {
        _tvNext.value = next
    }
}