package com.example.findride.ui.rides.list

import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findride.R
import com.example.findride.common.makeToast
import com.example.findride.databinding.FragmentUserRidesListBinding
import com.example.findride.isNetworkAvailable
import com.example.findride.model.Ride
import com.example.findride.model.ScreenState
import com.example.findride.onClick
import com.example.findride.subscribe
import com.example.findride.ui.base.BaseFragment
import com.example.findride.ui.rides.adapter.RidesAdapter
import com.example.findride.viewmodel.AddNewRideViewModel
import com.example.findride.viewmodel.UserRidesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserRidesListFragment : BaseFragment<FragmentUserRidesListBinding>() {
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentUserRidesListBinding
    get() = FragmentUserRidesListBinding::inflate
  
  private val viewModel: UserRidesViewModel by viewModels()
  private val newViewModel: AddNewRideViewModel by viewModels()
  
  private val ridesAdapter by lazy {
    context?.let { RidesAdapter(it) { ride -> showRideDetails(ride) } }
  }
  
  private fun showRideDetails(ride: Ride) {
    val action = UserRidesListFragmentDirections.actionUserRidesListFragmentToRideDetailsFragment(ride.id, 2)
    findNavController().navigate(action)
  }
  
  private fun navigateToAddNewRide() {
    findNavController().navigate(R.id.action_userRidesListFragment_to_startDestinationLocatorFragment)
    newViewModel.resetValues()
  }
  
  private fun setupRecycler() {
    with(binding.recycler) {
      layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
      adapter = ridesAdapter
    }
  }
  
  override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, menuInflater)
    menuInflater.inflate(R.menu.menu, menu)
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
  
  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_sign_out -> {
        signOut()
        true
      }
      else -> false
    }
  }
  
  private fun signOut() {
    viewModel.signOut(activity.isNetworkAvailable())
  }
  
  override fun onPostViewCreated() {
    setHasOptionsMenu(true)
    initBottomNavigation()
    setupRecycler()
    observeRides()
    (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    binding.btnAdd.onClick {
      navigateToAddNewRide()
    }
    viewModel.isUserSignedOut.observe(viewLifecycleOwner) {
      if (it) navigateToLogin()
    }
    binding.noInternet.tryAgainBtn.onClick {
      setupRecycler()
      observeRides()
    }
  }
  
  private fun navigateToLogin() {
    findNavController().navigate(R.id.action_userRidesListFragment_to_loginFragment)
  }
  
  private fun initBottomNavigation() {
    val bottomNavigationView = binding.bottomNavigation
    bottomNavigationView.selectedItemId = R.id.userRidesListFragment
    
    bottomNavigationView.setOnItemSelectedListener {
      when (it.itemId) {
        R.id.mainScreen -> {
          navigateToMyRides()
          return@setOnItemSelectedListener true
        }
        R.id.fragmentBookings -> {
          navigateToBookings()
          return@setOnItemSelectedListener true
        }
        else -> return@setOnItemSelectedListener false
      }
    }
    viewModel.notRespondedRides.observe(viewLifecycleOwner) {
      if(it.isNotEmpty()) bottomNavigationView.getOrCreateBadge(R.id.fragmentBookings)
    }
  }
  
  private fun navigateToBookings() {
    findNavController().navigate(R.id.action_userRidesListFragment_to_fragmentBookings)
  }
  
  private fun observeRides() {
    viewModel.getNotRespondedRides()
    viewModel.getUserRides(activity.isNetworkAvailable())
    viewModel.screenState.subscribe(viewLifecycleOwner, ::observeScreenState)
    viewModel.userRides.observe(viewLifecycleOwner) {
      ridesAdapter?.addRides(it)
    }
  }
  
  private fun navigateToMyRides() {
    findNavController().navigate(R.id.action_userRidesListFragment_to_mainScreen)
  }
  
}