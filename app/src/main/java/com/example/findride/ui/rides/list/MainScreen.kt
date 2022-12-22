package com.example.findride.ui.rides.list

import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findride.*
import com.example.findride.common.makeToast
import com.example.findride.databinding.FragmentMainScreenBinding
import com.example.findride.viewmodel.MainScreenViewModel
import com.example.findride.model.Ride
import com.example.findride.model.ScreenState
import com.example.findride.ui.base.BaseFragment
import com.example.findride.ui.rides.adapter.RidesAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainScreen : BaseFragment<FragmentMainScreenBinding>() {
  private val viewModel: MainScreenViewModel by viewModels()
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMainScreenBinding
    get() = FragmentMainScreenBinding::inflate
  
  private val ridesAdapter by lazy {
    context?.let { RidesAdapter(it) { ride -> showRideDetails(ride) } }
  }
  
  private fun showRideDetails(ride: Ride) {
    val action = MainScreenDirections.actionMainScreenToRideDetailsFragment(ride.id, 1)
    findNavController().navigate(action)
  }
  
  override fun onPostViewCreated() {
    setHasOptionsMenu(true)
    initBottomNavigation()
    observeData()
    (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    setupRecycler()
    observeRides()
    swipeToRefresh()
    binding.noInternet.tryAgainBtn.onClick {
      setupRecycler()
      observeRides()
    }
    binding.filter.onClick {
      findNavController().navigate(R.id.action_mainScreen_to_filterRidesFragment)
    }
  }
  
  private fun observeData() {
    viewModel.isUserSignedOut.observe(viewLifecycleOwner) {
      if (it) navigateToLogin()
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
      ScreenState.NO_INTERNET -> {
        binding.recycler.visibility = View.GONE
        binding.progress.root.visibility = View.GONE
        binding.noInternet.root.visibility = View.VISIBLE
      }
      ScreenState.ERROR -> {
        binding.noInternet.root.visibility = View.GONE
        binding.progress.root.visibility = View.GONE
        makeToast(getString(R.string.something_went_wrong))
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
    viewModel.getNotRespondedRides()
    viewModel.getAllRides(context.isNetworkAvailable())
    viewModel.screenState.subscribe(viewLifecycleOwner, ::observeScreenState)
    viewModel.rides.observe(viewLifecycleOwner) {
      ridesAdapter?.addRides(it)
    }
  }
  
  override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, menuInflater)
    menuInflater.inflate(R.menu.menu, menu)
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
  
  private fun initBottomNavigation() {
    val bottomNavigationView = binding.bottomNavigation
    bottomNavigationView.selectedItemId = R.id.mainScreen
    bottomNavigationView.setOnNavigationItemSelectedListener {
      when (it.itemId) {
        R.id.userRidesListFragment -> {
          navigateToMyRides()
          return@setOnNavigationItemSelectedListener true
        }
        R.id.fragmentBookings -> {
          navigateToBookings()
          return@setOnNavigationItemSelectedListener true
        }
        else -> return@setOnNavigationItemSelectedListener false
      }
    }
    viewModel.notRespondedRides.observe(viewLifecycleOwner) {
      if(it.isNotEmpty()) bottomNavigationView.getOrCreateBadge(R.id.fragmentBookings)
    }
  }
  
  private fun navigateToLogin() {
    findNavController().navigate(R.id.action_mainScreen_to_loginFragment)
  }
  
  private fun signOut() {
    viewModel.signOut()
  }
  
  private fun swipeToRefresh() {
    binding.container.setOnRefreshListener {
      observeRides()
      binding.container.isRefreshing = false;
    }
  }
  
  private fun navigateToBookings() {
    findNavController().navigate(R.id.action_mainScreen_to_fragmentBookings)
  }
  
  private fun navigateToMyRides() {
    findNavController().navigate(R.id.action_mainScreen_to_userRidesListFragment)
  }
}