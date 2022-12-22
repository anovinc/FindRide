package com.example.findride.ui.rides.booking

import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.findride.R
import com.example.findride.databinding.FragmentBookingsBinding
import com.example.findride.ui.base.BaseFragment
import com.example.findride.ui.rides.adapter.ScreenSlidePageAdapter
import com.example.findride.viewmodel.RideBookingViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingsContainerFragment : BaseFragment<FragmentBookingsBinding>() {
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBookingsBinding
    get() = FragmentBookingsBinding::inflate
  
  private var names = listOf("Waiting to respond", "My Bookings")
  private lateinit var viewPager: ViewPager2
  private val viewModel: RideBookingViewModel by viewModels()
  
  override fun onPostViewCreated() {
    viewPager = binding.pager
    val tabs = binding.tabs
    val pageAdapter = ScreenSlidePageAdapter(this)
    viewPager.adapter = pageAdapter
    TabLayoutMediator(tabs, viewPager) { tab, position ->
      tab.text = names[position]
    }.attach()
    initBottomNavigation()
    (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    setHasOptionsMenu(true)
    viewModel.getNotRespondedRides()
  }
  
  override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, menuInflater)
    menuInflater.inflate(R.menu.menu, menu)
  }
  
  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_sign_out -> {
        goToLogin()
        true
      }
      else -> false
    }
  }
  
  private fun goToLogin() {
    findNavController().navigate(R.id.action_fragmentBookings_to_loginFragment)
    viewModel.signOut()
  }
  
  private fun initBottomNavigation() {
    val bottomNavigationView = binding.bottomNavigation
    bottomNavigationView.selectedItemId = R.id.fragmentBookings
    bottomNavigationView.setOnNavigationItemSelectedListener {
      when (it.itemId) {
        R.id.userRidesListFragment -> {
          navigateToMyRides()
          return@setOnNavigationItemSelectedListener true
        }
        R.id.mainScreen -> {
          navigateToMainScreen()
          return@setOnNavigationItemSelectedListener true
        }
        else -> return@setOnNavigationItemSelectedListener false
      }
    }
    viewModel.bookedRides.observe(viewLifecycleOwner) {
      if(it.isNotEmpty()) bottomNavigationView.getOrCreateBadge(R.id.fragmentBookings)
    }
  }
  
  
  private fun navigateToMainScreen() {
    findNavController().navigate(R.id.action_fragmentBookings_to_mainScreen)
  }
  
  private fun navigateToMyRides() {
    findNavController().navigate(R.id.action_fragmentBookings_to_userRidesListFragment)
  }
}