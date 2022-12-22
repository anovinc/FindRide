package com.example.findride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findride.model.NewRideState
import com.example.findride.model.RideRouteLocationModel
import com.example.findride.interactor.RidesInteractorImpl
import com.example.findride.model.ScreenState
import com.example.findride.data.RideDirectionsRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNewRideViewModel @Inject constructor(
  private val ridesUseCaseImpl: RidesInteractorImpl,
  private val rideDirectionsRepository: RideDirectionsRepository
) : ViewModel() {
  
  private val _rideRoute = MutableLiveData<RideRouteLocationModel>()
  val rideRoute: LiveData<RideRouteLocationModel> = _rideRoute
  
  private val _newRide = MutableLiveData(NewRideState())
  val newRide: LiveData<NewRideState> = _newRide
  
  private val _isNewRideCreated = MutableLiveData<Boolean>()
  val isNewRideCreated: LiveData<Boolean> = _isNewRideCreated
  
  init {
    setStartDestination(rideDirectionsRepository.getStartLocation())
  }
  
  fun resetValues() {
    rideDirectionsRepository.resetValues()
  }
  
  fun createNewRide(isNetworkAvailable: Boolean, newRideState: NewRideState) {
    if (isNetworkAvailable) {
      viewModelScope.launch {
        _newRide.value = _newRide.value?.copy(screenState = ScreenState.LOADING)
        ridesUseCaseImpl.createNewRide(newRideState) { isSuccessful ->
          if (isSuccessful) {
            _isNewRideCreated.value = true
            _newRide.value = _newRide.value?.copy(screenState = ScreenState.IDLE)
            resetValues()
          } else {
            _newRide.value = _newRide.value?.copy(screenState = ScreenState.ERROR)
          }
        }
      }
    } else _newRide.value = _newRide.value?.copy(screenState = ScreenState.NO_INTERNET)
  }
  
  fun setRideTime(hours: Int, minutes: Int) {
    val timeInSes = (hours * 3600 + minutes * 60).toString()
    _newRide.value = _newRide.value?.copy(timeInSec = timeInSes)
    rideDirectionsRepository.setRideTime(timeInSes)
  }
  
  fun setRideFormattedTime(hours: Int, minutes: Int) {
    var _hours = hours.toString()
    var _minutes = minutes.toString()
    if (minutes < 10) _minutes = "0$_minutes"
    if (hours < 10) _hours = "0$_hours"
    _newRide.value = _newRide.value?.copy(timeFormatted = "$_hours:$_minutes")
  }
  
  fun setRideDate(day: Int, month: Int, year: Int) {
    _newRide.value = _newRide.value?.copy(day = day)
    _newRide.value = _newRide.value?.copy(month = month)
    _newRide.value = _newRide.value?.copy(year = year)
  }
  
  fun setStartDestination(startDestination: LatLng) {
    rideDirectionsRepository.setStartLocation(startDestination)
  }
  
  fun setEndDestination(endDestination: LatLng) {
    rideDirectionsRepository.setEndLocation(endDestination)
  }
  
  fun getRideHour(): Int {
    val totalTime = rideDirectionsRepository.getRideTime().toInt()
    val totalMinutes = totalTime / 60
    return totalMinutes / 60
  }
  
  fun getRideMinutes(): Int {
    val totalTime = rideDirectionsRepository.getRideTime().toInt()
    val totalMinutes = totalTime / 60
    return totalMinutes % 60
  }
  
  fun getStartDestination() = rideDirectionsRepository.getStartLocation()
  
  fun setRideRoute() {
    _rideRoute.value =
      RideRouteLocationModel(rideDirectionsRepository.getStartLocation(), rideDirectionsRepository.getEndLocation())
  }
}