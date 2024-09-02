package com.example.tateknew.ui.getData

interface OnAbonentClickListener {
    fun onAbonentClick(abonentId: Long){
        println("Abonent clicked with ID: $abonentId")
    }

    fun onMtrClick(abonentId: Long, nobjId: Int){
        print("MTR clicked with with AbonentId: $abonentId and MTRId: $nobjId")
    }
}