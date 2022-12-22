package com.example.findride.ui.rides

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findride.R
import com.example.findride.databinding.FragmentFilteredRidesListBinding
import com.example.findride.model.Ride
import com.example.findride.model.ScreenState
import com.example.findride.onClick
import com.example.findride.subscribe
import com.example.findride.ui.base.BaseFragment
import com.example.findride.ui.rides.adapter.RidesAdapter
import com.example.findride.viewmodel.FilterRidesViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilteredRidesListFragment : BaseFragment<FragmentFilteredRidesListBinding>() {
  
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFilteredRidesListBinding
    get() = FragmentFilteredRidesListBinding::inflate
  
  private val viewModel: FilterRidesViewModel by viewModels()
  
  private val ridesAdapter by lazy {
    context?.let { RidesAdapter(it) { ride -> showRideDetails(ride) } }
  }
  
  private fun showRideDetails(ride: Ride) {
    val action = FilteredRidesListFragmentDirections.actionFilteredRidesListFragmentToRideDetailsFragment(ride.id, 4)
    findNavController().navigate(action)
  }
  
  override fun onPostViewCreated() {
    initToolbar()
    viewModel.getRides()
    setupRecycler()
    observeRides()
    binding.filter.onClick {
      findNavController().navigate(R.id.action_filteredRidesListFragment_to_filterRidesFragment)
    }
  }
  
  private fun observeScreenState(screenState: ScreenState) {
    when (screenState) {
      ScreenState.LOADING -> {
        binding.progress.root.visibility = View.VISIBLE
        
      }
      ScreenState.IDLE -> {
        binding.progress.root.visibility = View.GONE
        binding.recycler.visibility = View.VISIBLE
      }
    }
  }
  
  private fun setupRecycler() {
    with(binding.recycler) {
      layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
      adapter = ridesAdapter
    }
  }
  
  private fun observeRides() {
    viewModel.screenState.subscribe(viewLifecycleOwner, ::observeScreenState)
    viewModel.ridesList.observe(viewLifecycleOwner) {
      ridesAdapter?.addRides(it)
      if(it.isEmpty()) binding.titleFirstScreen.visibility = View.VISIBLE
    }
  }
  
  private fun initToolbar() {
    activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.GONE
    (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    binding.toolbar.titleToolbar.text = "Filtered Rides"
    
    binding.toolbar.backBtn.onClick { goBack() }
  }
  
  private fun goBack() {
    findNavController().navigate(R.id.action_filteredRidesListFragment_to_mainScreen)
  }
  
  
}