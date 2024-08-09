package com.example.tateknew.ui.MTR


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tateknew.data.AppDatabase
import com.example.tateknew.data.MtrEntity
import com.example.tateknew.data.MtrWithAbonent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MtrsViewModel(application: Application) : AndroidViewModel(application) {
    private val mtrsDao = AppDatabase.getDatabase(application).mtrDao()
    private val _mtrs = MutableLiveData<List<MtrEntity>>()
    val mtrs: LiveData<List<MtrEntity>> get() = _mtrs

    fun loadMtrs(abonentId: Long) {
        viewModelScope.launch {
            val mtrsList = withContext(Dispatchers.IO) {
                mtrsDao.getMtrsByAbonentId(abonentId)
            }
            _mtrs.postValue(mtrsList)
        }
    }
}
