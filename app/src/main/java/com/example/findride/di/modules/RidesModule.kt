package com.example.findride.di.modules

import com.example.findride.data.FilterRidesRepository
import com.example.findride.interactor.RidesInteractorImpl
import com.example.findride.data.SharedPrefs
import com.example.findride.data.RideDirectionsRepository
import com.example.findride.interactor.RidesInteractor
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RidesModule {
  
  @Provides
  @Singleton
  fun provideRidesRepository(firestore: FirebaseFirestore, sharedPrefs: SharedPrefs):
    RidesInteractor {
    return RidesInteractorImpl(firestore, sharedPrefs)
  }
  
  @Provides
  @Singleton
  fun provideRidesRouteLocationRepository():
    RideDirectionsRepository {
    return RideDirectionsRepository()
  }
  
  @Provides
  @Singleton
  fun provideFilterRidesRepository():
    FilterRidesRepository {
    return FilterRidesRepository()
  }
}