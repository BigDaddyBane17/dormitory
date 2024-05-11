package com.example.tradeit.model.repository

import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.example.tradeit.model.Product
import com.example.tradeit.model.User
import com.example.tradeit.view.adapters.ProductImagePagerAdapter
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
import java.util.UUID

class Repository {
    private val database = Firebase.database.reference
    private val mDbRef = FirebaseDatabase.getInstance().reference
    private val mAuth = FirebaseAuth.getInstance()
    private val storageRef = Firebase.storage.reference
    val userDataLiveData: MutableLiveData<User> = MutableLiveData()
    val productsLiveData: MutableLiveData<List<Product>> = MutableLiveData()




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
                        userDataLiveData.postValue(user)
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

    fun getUserData(userId: String?) {
        userId?.let {
            mDbRef.child("Users").child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.child("username").value.toString()
                    val surname = snapshot.child("surname").value.toString()
                    val room = snapshot.child("room").value.toString()
                    val email = snapshot.child("email").value.toString()
                    val vkLink = snapshot.child("vkLink").value.toString()
                    val uid = snapshot.child("uid").value.toString()
                    val profileImage = snapshot.child("profileImage").value.toString()
                    val user = User(username, surname, email, uid, profileImage, vkLink, room)
                    userDataLiveData.postValue(user)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }
    fun updateUser(
        userId: String?,
        updatedUsername: String,
        updatedSurname: String,
        updatedRoom: String,
        updatedVkLink: String
    ) {
        userId?.let { userId ->
            val userRef = mDbRef.child("Users").child(userId)

            val updatedUserData = hashMapOf(
                "username" to updatedUsername,
                "surname" to updatedSurname,
                "room" to updatedRoom,
                "vkLink" to updatedVkLink
            )

            userRef.updateChildren(updatedUserData as Map<String, Any>)
                .addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {

                    } else {

                    }
                }
        }
    }

    fun updateRoomNumberForUserProducts(userId: String?, currentRoomNumber: String, updatedRoomNumber: String) {
        userId?.let { userId ->
            val productsRef = FirebaseDatabase.getInstance().reference.child("Products")
            productsRef.orderByChild("room").equalTo(currentRoomNumber).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (productSnapshot in snapshot.children) {
                        productSnapshot.ref.child("room").setValue(updatedRoomNumber)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Обработка ошибок
                }
            })
        }
    }

    fun getProductsByUserId(userId: String, callback: (List<Product>) -> Unit) {
        database.child("Products").orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val products = mutableListOf<Product>()
                    for (productSnapshot in snapshot.children) {
                        val currentProduct = productSnapshot.getValue(Product::class.java)
                        currentProduct?.let {
                            val imageUrls = mutableListOf<Uri>()
                            val imagesSnapshot = productSnapshot.child("images")
                            for (imageSnapshot in imagesSnapshot.children) {
                                val imageUrl = Uri.parse(imageSnapshot.getValue(String::class.java))
                                imageUrls.add(imageUrl)
                            }
                            it.imageUrls = imageUrls
                            products.add(it)
                        }
                    }
                    callback(products)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun addProduct(productName : String, productPrice : String, roomNumber : String, descriptionText : String, userId : String, productImageAdapter : ProductImagePagerAdapter) {
        val productId = database.child("Products").push().key
        val product = hashMapOf(
            "name" to productName,
            "price" to productPrice,
            "room" to roomNumber,
            "description" to descriptionText,
            "userId" to userId
        )
        productId?.let {
            database.child("Products").child(it).setValue(product)
                .addOnSuccessListener {

                }
                .addOnFailureListener {

                }
        }
        for (imageUri in productImageAdapter.getImagesUris()) {
            uploadImageToStorage(imageUri, productId)
        }
    }

    fun uploadImageToStorage(imageUri: Uri, productId: String?) {
        val imageRef: StorageReference = storageRef.child("product_images/$productId/${UUID.randomUUID()}")

        val uploadTask: UploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                productId?.let {
                    database.child("Products").child(it).child("images").push().setValue(uri.toString())
                }
            }
        }.addOnFailureListener { exception ->
            //Toast.makeText(requireContext(), "Ошибка при загрузке изображения: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }


    fun loadProductsForUser(userId: String) {
        mDbRef.child("Products").orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val productsList = mutableListOf<Product>()
                    for (productSnapshot in snapshot.children) {
                        val currentProduct = productSnapshot.getValue(Product::class.java)
                        currentProduct?.let {
                            val imageUrls = mutableListOf<Uri>()
                            val imagesSnapshot = productSnapshot.child("images")
                            for (imageSnapshot in imagesSnapshot.children) {
                                val imageUrl = Uri.parse(imageSnapshot.getValue(String::class.java))
                                imageUrls.add(imageUrl)
                            }
                            it.imageUrls = imageUrls
                            productsList.add(it)
                        }
                    }
                    productsLiveData.postValue(productsList)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    fun loadAllProducts() {
        mDbRef.child("Products").addValueEventListener(object :
            ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val productsList = mutableListOf<Product>()
                    for (productSnapshot in snapshot.children) {
                        val currentProduct = productSnapshot.getValue(Product::class.java)
                        currentProduct?.let {
                            val imageUrls = mutableListOf<Uri>()
                            val imagesSnapshot = productSnapshot.child("images")
                            for (imageSnapshot in imagesSnapshot.children) {
                                val imageUrl = Uri.parse(imageSnapshot.getValue(String::class.java))
                                imageUrls.add(imageUrl)
                            }
                            it.imageUrls = imageUrls
                            productsList.add(it)
                        }
                    }
                    productsLiveData.postValue(productsList)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }


}