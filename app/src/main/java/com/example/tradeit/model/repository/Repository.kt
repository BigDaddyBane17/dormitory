package com.example.tradeit.model.repository

import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.tradeit.model.Message
import com.example.tradeit.model.Product
import com.example.tradeit.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storage

class Repository {
    private val database = Firebase.database.reference
    private val mDbRef = FirebaseDatabase.getInstance().reference
    private val mAuth = FirebaseAuth.getInstance()
    private val storageRef=Firebase.storage.reference

    val userInfoLiveData: MutableLiveData<User> = MutableLiveData()


    private val allUsersList = ArrayList<User>()
    private val allUsersListLiveData: MutableLiveData<ArrayList<User>> by lazy { MutableLiveData<ArrayList<User>>() }


    private val allProductsList = ArrayList<Product>()
    private val allProductsListLiveData: MutableLiveData<ArrayList<Product>> by lazy { MutableLiveData<ArrayList<Product>>() }


    private val myProductsList = ArrayList<Product>()
    private val myProductsListLiveData: MutableLiveData<ArrayList<Product>> by lazy { MutableLiveData<ArrayList<Product>>() }

    private val allMessagesList = ArrayList<Message>()
    private val allMessagesListLiveData: MutableLiveData<ArrayList<Message>> by lazy { MutableLiveData<ArrayList<Message>>() }



    fun loadUserInfo() {
        mAuth.currentUser?.uid?.let { userId ->
            mDbRef.child("Users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val username = snapshot.child("username").value.toString()
                        val surname = snapshot.child("surname").value.toString()
                        val room = snapshot.child("room").value.toString()
                        val email = snapshot.child("email").value.toString()
                        val vkLink = snapshot.child("vkLink").value.toString()
                        val uid = snapshot.child("uid").value.toString()
                        val profileImage = snapshot.child("profileImage").value.toString()
                        val user = User(username, surname, email, uid, profileImage, vkLink, room)
                        userInfoLiveData.postValue(user)
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
    }
    fun uploadImage(filePath: Uri?) {
        filePath?.let { filePath ->
            val uid = mAuth.currentUser?.uid
            val storageReference = FirebaseStorage.getInstance().getReference("images/$uid")

            storageReference.putFile(filePath)
                .addOnSuccessListener { taskSnapshot ->

                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        FirebaseDatabase.getInstance().getReference("Users").child(uid!!)
                            .child("profileImage").setValue(uri.toString())
                    }
                }
        }
    }

}