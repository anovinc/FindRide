package com.example.findride.ui.rides

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.findride.R
import com.example.findride.common.makeToast
import com.example.findride.databinding.BottomSheetDialogFilterRidesBinding
import com.example.findride.onClick
import com.example.findride.viewmodel.FilterRidesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.math.roundToInt

@AndroidEntryPoint
class FilterRidesBottomSheetDialog : BottomSheetDialogFragment() {
  
  private var _binding: BottomSheetDialogFilterRidesBinding? = null
  private val binding get() = _binding!!
  
  private val viewModel: FilterRidesViewModel by viewModels()
  
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = BottomSheetDialogFilterRidesBinding.inflate(inflater, container, false)
    return binding.root
  }
  
  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initListeners()
    observeData()
    
  }
  
  private fun observeData() {
    viewModel.filterRide.observe(viewLifecycleOwner) {
      if (it.day != 0) binding.dateValue.setText("${it.day}.${it.month}.${it.year}.")
      if (it.startPrice != 0 || it.endPrice != 999) binding.priceValue.text = "${it.startPrice} - ${it.endPrice} KN"
    }
    viewModel.ridesList.observe(viewLifecycleOwner) {
      findNavController().navigate(R.id.action_filterRidesFragment_to_filteredRidesListFragment)
    }
  }
  
  
  private fun initListeners() {
    binding.dateValue.onClick {
      showDatePicker()
    }
    binding.priceSlider.addOnChangeListener { slider, value, fromUser ->
      viewModel.setPriceRange(slider.values[0].roundToInt(), slider.values[1].roundToInt())
    }
    binding.btnFilter.onClick {
      if (binding.dateValue.text.isNullOrEmpty()) makeToast("Please select date.")
      else {
        viewModel.setRideRoute(binding.rideDirectionValueStart.text.toString(), binding.rideDirectionValueEnd.text.toString())
        context?.let { viewModel.filterRides(it) }
      }
    }
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
}