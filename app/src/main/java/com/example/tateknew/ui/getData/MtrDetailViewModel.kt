package com.example.tateknew.ui.getData

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tateknew.data.MtrEntity

class MtrDetailViewModel : ViewModel() {

    private val _mtrs = MutableLiveData<List<MtrEntity>>()
    val mtrs: LiveData<List<MtrEntity>> get() = _mtrs

    fun updateMtrs(mtrList: List<MtrEntity>) {
        _mtrs.value = mtrList
    }
}
