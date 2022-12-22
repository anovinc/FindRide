package com.example.findride.interactor

import android.net.Uri
import com.example.findride.common.*
import com.example.findride.data.SharedPrefs
import com.example.findride.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject


class AuthenticationInteractor @Inject constructor(
  private val firebaseAuth: FirebaseAuth,
  private val fireStore: FirebaseFirestore,
  private val firebaseStorage: FirebaseStorage,
  private val sharedPrefs: SharedPrefs
) {
  
  private fun saveUserId() {
    try {
      val userRef = sharedPrefs.getUser()?.let { fireStore.collection(USERS).document(it) }
      userRef?.get()?.addOnSuccessListener { documentSnapshot ->
        val user = documentSnapshot.toObject<User>()
        if (user != null) sharedPrefs.saveUserId(user.id)
      }
    } catch (e: Exception) {
      makeToast(e.message.toString())
    }
  }
  
  suspend fun login(username: String, password: String, onResult: (Boolean) -> Unit) {
    withContext(Dispatchers.IO) {
      try {
        firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener {
          if (it.isSuccessful) {
            sharedPrefs.saveUser(firebaseAuth.uid.toString())
            onResult(it.isSuccessful)
            saveUserId()
          }
        }
          .addOnFailureListener {
            onResult(false)
            makeToast(it.message.toString())
          }
      } catch (e: Exception) {
        onResult(false)
        makeToast(e.message.toString())
      }
    }
  }
  
  suspend fun register(
    username: String, password: String, name: String, surname: String, phoneNumber: String,profileImage: String, onResult:
      (Boolean) ->
    Unit
  ) {
    val id = UUID.randomUUID().toString()
    withContext(Dispatchers.IO) {
      try {
        if (profileImage != EMPTY) {
          val uploadTask = firebaseStorage.reference.child("$IMAGES/$username").putFile(Uri.parse(profileImage))
          uploadTask.addOnFailureListener {
          }
            .addOnSuccessListener {
              val url = it.storage.downloadUrl
              while (!url.isComplete) {
              }
              val user = User(
                id,
                username,
                name,
                surname,
                phoneNumber,
                url.result.toString()
              )
              firebaseAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener {
                if (it.isSuccessful) {
                  fireStore.collection(USERS).document(firebaseAuth.uid.toString()).set(user)
                  onResult(it.isSuccessful)
                  makeToast("Registration successful!")
                  sharedPrefs.saveUser(firebaseAuth.uid.toString())
                  sharedPrefs.saveUserId(id)
                }
              }
                .addOnFailureListener {
                  onResult(false)
                  makeToast(it.message.toString())
                }
            }
        } else {
          val user = User(
            id,
            username,
            name,
            surname,
            phoneNumber,
            DEFAULT_PHOTO
          )
          firebaseAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener {
            if (it.isSuccessful) {
              fireStore.collection(USERS).document(firebaseAuth.uid.toString()).set(user)
              onResult(it.isSuccessful)
              makeToast("Registration successful!")
              sharedPrefs.saveUser(firebaseAuth.uid.toString())
              sharedPrefs.saveUserId(id)
            }
          }
            .addOnFailureListener {
              onResult(false)
              makeToast(it.message.toString())
            }
        }
      } catch (e: Exception) {
        onResult(false)
        makeToast(e.message.toString())
      }
    }
  }
  
  suspend fun signOut() {
    withContext(Dispatchers.IO) {
      firebaseAuth.signOut()
      sharedPrefs.saveUser(EMPTY)
      sharedPrefs.saveUserId(EMPTY)
    }
  }
}