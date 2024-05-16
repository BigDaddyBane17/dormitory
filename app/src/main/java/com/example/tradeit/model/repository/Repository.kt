package com.example.tradeit.model.repository
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tradeit.model.Message
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
    val myProductsLiveData: MutableLiveData<List<Product>> = MutableLiveData()



    fun registerUser(email: String, password: String, name: String, surname: String, room: String, vkLink: String, onSuccess: () -> Unit, onError: () -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    currentUser?.let { user ->
                        val userInfo = hashMapOf(
                            "uid" to user.uid,
                            "email" to email,
                            "username" to name,
                            "surname" to surname,
                            "room" to room,
                            "vkLink" to vkLink,
                            "profileImage" to ""
                        )

                        FirebaseDatabase.getInstance().reference.child("Users").child(user.uid)
                            .setValue(userInfo)
                            .addOnCompleteListener { databaseTask ->
                                if (databaseTask.isSuccessful) {
                                    onSuccess()
                                } else {
                                    onError()
                                }
                            }
                    }
                } else {
                    onError()
                }
            }
    }

    fun loginUser(email: String, password: String, onSuccess: () -> Unit) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                }
            }
    }





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

    fun addProduct(productName : String, productPrice : String, roomNumber : String, descriptionText : String, userId : String, productImageAdapter : ProductImagePagerAdapter) {
        val productId = database.child("Products").push().key
        val product = hashMapOf(
            "name" to productName,
            "price" to productPrice,
            "room" to roomNumber,
            "description" to descriptionText,
            "userId" to userId,
            "productId" to productId
        )
        productId?.let {
            database.child("Products").child(it).setValue(product)
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
                    myProductsLiveData.postValue(productsList)
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


    fun getLastMessage(uid: String, onComplete: (String?, Long?) -> Unit) {
        mDbRef.child("Chats").child(mAuth.currentUser!!.uid + uid)
            .child("Messages").orderByChild("timestamp").limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var lastMessage: String? = null
                    var lastMessageTime: Long? = null
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        lastMessage = message?.message
                        lastMessageTime = message?.timestamp
                    }
                    if (lastMessage == null) {
                        lastMessage = "Начните общаться!"
                    }
                    onComplete(lastMessage, lastMessageTime)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun getUsersWithLastMessage(excludeCurrentUser: Boolean, onComplete: (List<User>) -> Unit) {
        val currentUserUid = mAuth.currentUser?.uid
        mDbRef.child("Users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = mutableListOf<User>()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let {
                        if (excludeCurrentUser && user.uid == currentUserUid) {
                            return@let
                        }
                        user.uid?.let { it1 ->
                            getLastMessage(it1) { lastMessage, lastMessageTime ->
                                user.lastMessage = lastMessage
                                user.lastMessageTime = lastMessageTime
                                userList.add(user)
                                if (userList.size == snapshot.childrenCount.toInt() - (if (excludeCurrentUser) 1 else 0)) {
                                    // Сортируем список по времени последнего сообщения (пользователи с последними сообщениями первыми)
                                    val sortedList = userList.sortedByDescending { it.lastMessageTime }
                                    onComplete(sortedList)
                                }
                            }
                        }
                    }
                }
                if (snapshot.childrenCount == 0L || (!excludeCurrentUser && userList.size == snapshot.childrenCount.toInt())) {
                    onComplete(userList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    fun getMessageList(senderRoom: String): LiveData<List<Message>> {
        val messageList = MutableLiveData<List<Message>>()
        mDbRef.child("Chats").child(senderRoom).child("Messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = ArrayList<Message>()
                    for(postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        message?.let { list.add(it) }
                    }
                    messageList.value = list
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        return messageList
    }



    fun sendMessage(senderRoom: String, receiverRoom: String, messageObject: Message) {
        database.child("Chats").child(senderRoom).child("Messages").push()
            .setValue(messageObject).addOnSuccessListener {
                database.child("Chats").child(receiverRoom).child("Messages").push()
                    .setValue(messageObject)
            }
    }

    fun deleteProduct(productId : String) {
        database.child("Products").child(productId).removeValue()
    }


}


