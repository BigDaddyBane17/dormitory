package com.example.tradeit.model

import android.net.Uri

data class Product(
    val name: String = "",
    val price: String = "",
    val room : String = "",
    var imageUrls: MutableList<Uri> = mutableListOf()
)