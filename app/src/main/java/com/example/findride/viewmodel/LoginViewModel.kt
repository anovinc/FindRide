package com.example.findride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findride.model.ScreenState
import com.example.findride.interactor.AuthenticationInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authenticationInteractor: AuthenticationInteractor) : ViewModel() {
  
  private val _isUserLoggedIn = MutableLiveData<Boolean>()
  val isUserLoggedIn: LiveData<Boolean> = _isUserLoggedIn
  
  private val _userCredentials = MutableLiveData<Boolean>()
  val userCredentials: LiveData<Boolean> = _userCredentials
  
  private val _screenState = MutableLiveData<ScreenState>()
  val screenState: LiveData<ScreenState> = _screenState
  
  fun login(username: String, password: String, isNetworkAvailable: Boolean) {
    if (isNetworkAvailable) {
      _screenState.value = ScreenState.LOADING
      viewModelScope.launch {
        authenticationInteractor.login(username, password) { isSuccessful ->
          if (isSuccessful) {
            _isUserLoggedIn.value = true
          }
          _screenState.value = ScreenState.IDLE
        }
      }
    }
    else _screenState.value = ScreenState.NO_INTERNET
  }
  
  fun checkUserCredentials(username: String, password: String) {
    _userCredentials.value = username.isNotEmpty() && password.isNotEmpty()
  }
}