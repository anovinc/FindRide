package com.example.findride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findride.common.EMPTY
import com.example.findride.model.ScreenState
import com.example.findride.interactor.AuthenticationInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val authenticationInteractor: AuthenticationInteractor) : ViewModel() {
  
  private val _isUserRegistered = MutableLiveData<Boolean>()
  val isUserRegistered: LiveData<Boolean> = _isUserRegistered
  
  private val _userCredentials = MutableLiveData<Boolean>()
  val userCredentials: LiveData<Boolean> = _userCredentials
  
  private val _profilePicture = MutableLiveData(EMPTY)
  val profilePicture: LiveData<String> = _profilePicture
  
  private val _screenState = MutableLiveData<ScreenState>()
  val screenState: LiveData<ScreenState> = _screenState
  
  
  fun register(
    username: String,
    password: String,
    name: String,
    surname: String,
    phoneNumber: String,
    profileImage: String,
    isNetworkAvailable: Boolean
  ) {
    if (isNetworkAvailable) {
      viewModelScope.launch {
        _screenState.value = ScreenState.LOADING
        authenticationInteractor.register(username, password, name, surname, phoneNumber, profileImage) { isSuccessful ->
          if (isSuccessful) {
            _isUserRegistered.value = true
            _screenState.value = ScreenState.IDLE
          } else _screenState.value = ScreenState.IDLE
        }
      }
    } else _screenState.value = ScreenState.NO_INTERNET
  }
  
  fun checkUserCredentials(username: String, password: String, name: String, surname: String, phoneNumber: String) {
    _userCredentials.value = username.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && surname.isNotEmpty() &&
      phoneNumber.isNotEmpty()
  }
  
  fun setProfileImage(uri: String) {
    _profilePicture.value = uri
  }
}