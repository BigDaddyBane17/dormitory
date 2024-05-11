package com.example.tradeit.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.tradeit.model.User
import com.example.tradeit.model.repository.Repository

class tradeViewModel : ViewModel() {

    private val repository = Repository()

    val userInfoLiveData: LiveData<User> = repository.userInfoLiveData


    fun loadUserInfo() {
        repository.loadUserInfo()
    }

    fun uploadImage(filePath: Uri?) {
        repository.uploadImage(filePath)
    }

}