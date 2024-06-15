package com.example.tateknew.ui.getData

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class getDataViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Для загрузки новых задач нажмите на кнопку"
    }
    val text: LiveData<String> = _text
}