package com.example.findride.di.modules

import com.example.findride.interactor.AuthenticationInteractor
import com.example.findride.data.SharedPrefs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
  
  @Provides
  @Singleton
  fun provideFirebaseAuthentication() = FirebaseAuth.getInstance()
  
  @Provides
  @Singleton
  fun provideFirestore() = FirebaseFirestore.getInstance()
  
  @Provides
  @Singleton
  fun provideFirebaseStorage() = FirebaseStorage.getInstance()
  
  @Provides
  @Singleton
  fun provideAuthenticationInteractor(
    firebaseAuth: FirebaseAuth, firestore: FirebaseFirestore, firebaseStorage:
    FirebaseStorage, sharedPrefs: SharedPrefs
  ):
    AuthenticationInteractor {
    return AuthenticationInteractor(firebaseAuth, firestore, firebaseStorage, sharedPrefs)
  }
  
}