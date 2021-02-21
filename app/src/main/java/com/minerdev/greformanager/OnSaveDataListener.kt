package com.minerdev.greformanager

interface OnSaveDataListener {
    fun checkData(): Boolean
    fun saveData()
}