package com.example.tradeit.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import android.net.Uri

@Parcelize
data class Product(
    val name: String = "",
    val price: String = "",
    val room : String = "",
    val description : String = "",
    var imageUrls: MutableList<Uri> = mutableListOf(),
    val userId : String = "",
    val productId : String = ""
) : Parcelable



