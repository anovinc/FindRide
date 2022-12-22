package com.example.findride.ui.rides

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.findride.model.NewRideState
import com.example.findride.R
import com.example.findride.common.makeToast
import com.example.findride.databinding.FragmentAddNewRideBinding
import com.example.findride.isNetworkAvailable
import com.example.findride.model.ScreenState
import com.example.findride.onClick
import com.example.findride.ui.base.BaseFragment
import com.example.findride.viewmodel.AddNewRideViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddNewRideFragment : BaseFragment<FragmentAddNewRideBinding>() {
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddNewRideBinding
    get() = FragmentAddNewRideBinding::inflate
  
  private val timePickerDialogListener: TimePickerDialog.OnTimeSetListener =
    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
      viewModel.setRideTime(hourOfDay, minute)
      viewModel.setRideFormattedTime(hourOfDay, minute)
    }
  private val viewModel: AddNewRideViewModel by viewModels()
  
  private var geocoder: Geocoder? = null
  
  override fun onPostViewCreated() {
    initToolbar()
    initListeners()
    initDropdowns()
    viewModel.setRideRoute()
    geocoder = Geocoder(activity, Locale.getDefault())
    viewModel.isNewRideCreated.observe(viewLifecycleOwner) {
      navigateToMyRides()
    }
    viewModel.newRide.observe(viewLifecycleOwner) {
      if (it.timeFormatted != "") binding.timeValue.setText(it.timeFormatted)
      if (it.day != 0) binding.dateValue.setText("${it.day}.${it.month + 1}.${it.year}.")
      observeScreenState(it.screenState)
    }
    viewModel.rideRoute.observe(viewLifecycleOwner) {
      val startAddress: MutableList<Address>? = geocoder?.getFromLocation(
        it!!.startDestination.latitude, it!!
          .startDestination.longitude, 1
      )
      val endAddress: MutableList<Address>? = geocoder?.getFromLocation(
        it!!.endLocation.latitude, it!!
          .endLocation.longitude, 1
      )
      if (it.startDestination != LatLng(0.0, 0.0)) {
        val lastIndexFirst = startAddress!![0].getAddressLine(0).lastIndexOf(",")
        val start = startAddress!![0].getAddressLine(0).substring(0, lastIndexFirst)
        val lastIndexSecond = endAddress!![0].getAddressLine(0).lastIndexOf(",")
        val end = endAddress!![0].getAddressLine(0).substring(0, lastIndexSecond)
        binding.rideDirectionValueStart.setText(start)
        binding.rideDirectionValueEnd.setText(end)
      }
    }
  }
  private fun navigateToMyRides() {
    findNavController().navigate(R.id.action_addNewRideFragment_to_userRidesListFragment)
  }
  
  
  private fun initListeners() {
    with(binding) {
      dateValue.onClick {
        showDatePicker()
      }
      timeValue.onClick {
        showTimePicker()
      }
      btnAdd.onClick {
        if (handleInputs()) viewModel.createNewRide(context.isNetworkAvailable(), createRide())
        else makeToast(getString(R.string.empty_fields))
      }
    }
  }
  
  private fun observeScreenState(screenState: ScreenState) {
    when (screenState) {
      ScreenState.LOADING -> {
        binding.progress.root.visibility = View.VISIBLE
        binding.container.visibility = View.VISIBLE
        
      }
      ScreenState.IDLE -> {
        binding.container.visibility = View.VISIBLE
        binding.progress.root.visibility = View.GONE
      }
      ScreenState.NO_INTERNET -> {
        binding.progress.root.visibility = View.GONE
        binding.container.visibility = View.GONE
      }
      ScreenState.ERROR -> {
        binding.progress.root.visibility = View.GONE
        makeToast(getString(R.string.something_went_wrong))
      }
      
    }
  }
  
  private fun initDropdowns() {
    val availableSeats = resources.getStringArray(R.array.available_seats)
    val arrayAdapterSeats = ArrayAdapter(requireContext(), R.layout.dropdown_item, availableSeats)
    binding.numOfAvailableSeatsValue.setAdapter(arrayAdapterSeats)
  }
  
  private fun showTimePicker() {
    val timePicker = TimePickerDialog(
      context, timePickerDialogListener, viewModel.getRideHour(), viewModel
        .getRideMinutes(), true
    )
    timePicker.show()
  }
  
  private fun showDatePicker() {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    
    val datePicker = context?.let {
      DatePickerDialog(it, { _, year, monthOfYear, dayOfMonth ->
        viewModel.setRideDate(dayOfMonth, monthOfYear, year)
      }, year, month, day)
    }
    datePicker?.datePicker?.minDate = System.currentTimeMillis()
    datePicker?.show()
  }
  
  private fun createRide(): NewRideState {
    return NewRideState(
      viewModel.newRide.value!!.day,
      viewModel.newRide.value!!.month + 1,
      viewModel.newRide.value!!.year,
      viewModel.newRide.value!!.timeInSec,
      viewModel.newRide.value!!.timeFormatted,
      viewModel.rideRoute.value!!.startDestination.latitude.toString(),
      viewModel.rideRoute.value!!.startDestination.longitude.toString(),
      viewModel.rideRoute.value!!.endLocation.latitude.toString(),
      viewModel.rideRoute.value!!.endLocation.longitude.toString(),
      binding.numOfAvailableSeatsValue
        .text.toString
          ().toInt(),
      binding.desc.text.toString(),
      binding.priceValue.text.toString().toInt()
    )
  }
  
  private fun initToolbar() {
    activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.GONE
    (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    binding.toolbar.titleToolbar.text = getString(R.string.add_new_ride)
    binding.toolbar.backBtn.onClick {
      goBack()
    }
  }
  
  private fun handleInputs(): Boolean {
    with(binding) {
      return !(rideDirectionValueStart.text.toString().isNullOrEmpty() || numOfAvailableSeatsValue.text.toString().isNullOrEmpty()
        || priceValue.text.toString().isNullOrEmpty() || timeValue.text.toString().isNullOrEmpty() || dateValue.text.toString()
        .isNullOrEmpty())
    }
  }
  
  private fun goBack() {
    findNavController().navigate(R.id.action_addNewRideFragment_to_pickFinalRideDirectionLocatorFragment)
  }
}