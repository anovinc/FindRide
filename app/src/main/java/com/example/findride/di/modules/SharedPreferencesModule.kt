package com.example.findride.di.modules

import android.app.Application
import android.content.Context
import com.example.findride.data.SharedPrefs
import com.example.findride.data.SharedPrefsImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class SharedPreferencesModule {
  
  @Binds
  abstract fun bindContext(application: Application): Context
  
  @Binds
  abstract fun bindSharedPreferences(sharedPrefsImpl: SharedPrefsImpl): SharedPrefs
  
}