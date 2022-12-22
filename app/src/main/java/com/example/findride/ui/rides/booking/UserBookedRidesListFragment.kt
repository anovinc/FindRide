package com.example.findride.ui.rides.booking

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findride.R
import com.example.findride.common.makeToast
import com.example.findride.databinding.FragmentUserBookedRidesListBinding
import com.example.findride.model.RideBooking
import com.example.findride.model.ScreenState
import com.example.findride.subscribe
import com.example.findride.ui.base.BaseFragment
import com.example.findride.ui.rides.booking.adapter.UserRequestedAdapter
import com.example.findride.viewmodel.BookedRidesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserBookedRidesListFragment : BaseFragment<FragmentUserBookedRidesListBinding>() {
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentUserBookedRidesListBinding
    get() = FragmentUserBookedRidesListBinding::inflate
  private val viewModel: BookedRidesViewModel by viewModels()
  
  private val userRequestedAdapter by lazy {
    context?.let {
      UserRequestedAdapter(
        it,
        onItemClicked = { ride -> showRideDetails(ride) },
        onPhoneCallClicked = { ride -> callPerson(ride) },
        onCancelRideClicked = { rideBooking -> cancelBooking(rideBooking) })
    }
  }
  
  private fun cancelBooking(rideBooking: RideBooking) {
    viewModel.cancelBooking(rideBooking.id)
  }
  
  private fun callPerson(rideBooking: RideBooking) {
    val hasPermission = activity?.let {
      ContextCompat.checkSelfPermission(
        it,
        Manifest.permission.CALL_PHONE
      )
    } == PackageManager.PERMISSION_GRANTED
    if (!hasPermission) {
      requestPermission()
    } else {
      val intent = Intent(Intent.ACTION_CALL)
      intent.data = Uri.parse("tel:${rideBooking.ride.phoneNumber}")
      startActivity(intent)
    }
  }
  
  private fun requestPermission() {
    activity?.let {
      ActivityCompat.requestPermissions(
        it,
        arrayOf(Manifest.permission.CALL_PHONE),
        200
      )
    }
  }
  
  private fun showRideDetails(ride: RideBooking) {
    val action = BookingsContainerFragmentDirections.actionFragmentBookingsToRideDetailsFragment(ride.ride.id, 3)
    findNavController().navigate(action)
  }
  
  
  private fun setupRecycler() {
    with(binding.recycler) {
      layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
      adapter = userRequestedAdapter
    }
  }
  
  override fun onResume() {
    super.onResume()
    observeRides()
  }
  
  private fun observeScreenState(screenState: ScreenState) {
    when (screenState) {
      ScreenState.LOADING -> {
        binding.progress.root.visibility = View.VISIBLE
        binding.noInternet.root.visibility = View.GONE
      }
      ScreenState.IDLE -> {
        binding.progress.root.visibility = View.GONE
        binding.noInternet.root.visibility = View.GONE
        binding.recycler.visibility = View.VISIBLE
      }
      
      ScreenState.ERROR -> {
        binding.noInternet.root.visibility = View.GONE
        binding.progress.root.visibility = View.GONE
        makeToast(getString(R.string.something_went_wrong))
      }
    }
  }
  
  
  override fun onPostViewCreated() {
    setupRecycler()
    observeRides()
    viewModel.isRideListUpdated.observe(viewLifecycleOwner) {
      if (it) observeRides()
    }
  }
  
  private fun observeRides() {
    viewModel.getUserRequestedRidesBookings()
    viewModel.screenState.subscribe(viewLifecycleOwner, ::observeScreenState)
    viewModel.userBookedRides.observe(viewLifecycleOwner) {
      userRequestedAdapter?.addRides(it)
    }
  }
}