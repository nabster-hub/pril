package com.example.tateknew.ui.Tps

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.tateknew.data.AppDatabase
import com.example.tateknew.data.TpsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TPsViewModel(application: Application) : AndroidViewModel(application) {
    private val TpsDao = AppDatabase.getDatabase(application).objectDao()
    val allObjects: LiveData<List<TpsEntity>> = TpsDao.getAllObjects()

    // If you want to insert or perform any database operations, you can use viewModelScope.launch
    fun insert(tpsEntity: TpsEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            TpsDao.insertObject(tpsEntity)
        }
    }
}
