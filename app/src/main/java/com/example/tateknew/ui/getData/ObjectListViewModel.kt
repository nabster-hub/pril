package com.example.tateknew.ui.getData

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.tateknew.data.AppDatabase
import com.example.tateknew.data.ObjectEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ObjectListViewModel(application: Application) : AndroidViewModel(application) {
    private val objectDao = AppDatabase.getDatabase(application).objectDao()
    val allObjects: LiveData<List<ObjectEntity>> = objectDao.getAllObjects()

    // If you want to insert or perform any database operations, you can use viewModelScope.launch
    fun insert(objectEntity: ObjectEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            objectDao.insertObject(objectEntity)
        }
    }
}
