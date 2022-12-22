package com.example.findride.ui.rides.booking

import android.view.*
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findride.R
import com.example.findride.common.makeToast
import com.example.findride.databinding.FragmentRequestedRidesListBinding
import com.example.findride.model.RideBooking
import com.example.findride.model.ScreenState
import com.example.findride.subscribe
import com.example.findride.ui.base.BaseFragment
import com.example.findride.ui.rides.booking.adapter.BookedRideAdapter
import com.example.findride.viewmodel.BookedRidesViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestedRidesListFragment : BaseFragment<FragmentRequestedRidesListBinding>() {
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRequestedRidesListBinding
    get() = FragmentRequestedRidesListBinding::inflate
  
  private val viewModel: BookedRidesViewModel by viewModels()
  private val bookedRideAdapter by lazy {
    context?.let {
      BookedRideAdapter(it,
        onAcceptClick = { rideBooking -> acceptClick(rideBooking) },
        onDeclineClick = { rideBooking -> declineClick(rideBooking) }
      )
    }
  }
  
  private fun declineClick(rideBooking: RideBooking) {
    viewModel.declineBooking(rideBooking.id)
    observeRides()
    
  }
  
  private fun acceptClick(rideBooking: RideBooking) {
    viewModel.acceptBooking(rideBooking.id)
    observeRides()
  }
  
  private fun swipeToRefresh() {
    binding.container.setOnRefreshListener {
      observeRides()
      binding.container.isRefreshing = false;
    }
  }
  
  override fun onResume() {
    super.onResume()
    observeRides()
  }
  
  private fun setupRecycler() {
    with(binding.recycler) {
      layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
      adapter = bookedRideAdapter
    }
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
    swipeToRefresh()
  }
  
  private fun observeRides() {
    viewModel.getRequestedBookings()
    viewModel.screenState.subscribe(viewLifecycleOwner, ::observeScreenState)
    viewModel.bookedRides.observe(viewLifecycleOwner) {
      if(it.isEmpty()) parentFragment?.view?.rootView?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.removeBadge(R.id.fragmentBookings)
      bookedRideAdapter?.addRides(it)
    }
  }
}