package com.example.tateknew.ui.MTR

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.tateknew.data.MtrDao
import kotlinx.coroutines.Dispatchers

class MtrDetailViewModel(private val mtrDao: MtrDao) : ViewModel() {

    fun getMtrDetail(mtrId: Long) = liveData(Dispatchers.IO) {
        emit(mtrDao.getMtr(mtrId))
    }
}
