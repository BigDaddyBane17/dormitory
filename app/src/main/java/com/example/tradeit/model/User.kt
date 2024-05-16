package com.example.tradeit.model

data class User(
    var username : String? = null,
    var surname : String? = null,
    var email : String? = null,
    var uid: String? = null,
    var profileImage: String? = null,
    var vkLink : String? = null,
    var room : String? = null,
    var lastMessage: String? = null,
    var lastMessageTime: Long? = null,
)

