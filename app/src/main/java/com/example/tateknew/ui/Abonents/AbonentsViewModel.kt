package com.example.tateknew.ui.Abonents

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tateknew.data.AppDatabase
import com.example.tateknew.data.MtrWithAbonent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AbonentsViewModel(application: Application) : AndroidViewModel(application) {
    private val abonentsDao = AppDatabase.getDatabase(application).mtrDao()
    private val _abonents = MutableLiveData<List<MtrWithAbonent>>()
    val abonents: LiveData<List<MtrWithAbonent>> get() = _abonents

    fun loadAbonents(objectId: Int) {
        viewModelScope.launch {
            val abonentsList = withContext(Dispatchers.IO) {
                abonentsDao.getAbonents(objectId)
            }
            _abonents.postValue(abonentsList)
        }
    }
}
