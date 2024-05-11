package com.example.tradeit.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.tradeit.model.Product
import com.example.tradeit.model.User
import com.example.tradeit.model.repository.Repository
import com.example.tradeit.view.adapters.ProductImagePagerAdapter

class TradeViewModel : ViewModel() {

    private val repository = Repository()

    val userDataLiveData: LiveData<User> = repository.userDataLiveData
    val productsLiveData: LiveData<List<Product>> = repository.productsLiveData
    val AllProductsLiveData: LiveData<List<Product>> = repository.productsLiveData


    fun loadUserInfo() {
        repository.loadUserInfo()
    }

    fun uploadImage(filePath: Uri?) {
        repository.uploadImage(filePath)
    }

    fun getUserData(userId: String?) {
        repository.getUserData(userId)
    }

    fun updateUser(
        userId: String?,
        updatedUsername: String,
        updatedSurname: String,
        updatedRoom: String,
        updatedVkLink: String,
        currentRoomNumber : String = ""
    ) {
        repository.updateUser(userId, updatedUsername, updatedSurname, updatedRoom, updatedVkLink)
        repository.updateRoomNumberForUserProducts(userId, currentRoomNumber, updatedRoom)
    }

    fun addProduct(productName : String, productPrice : String, roomNumber : String, descriptionText : String, userId : String, productImageAdapter : ProductImagePagerAdapter) {
        repository.addProduct(productName, productPrice, roomNumber, descriptionText, userId, productImageAdapter)
    }
    fun loadProductsForUser(userId: String) {
        repository.loadProductsForUser(userId)
    }

    fun loadAllProducts() {
        repository.loadAllProducts()
    }

}