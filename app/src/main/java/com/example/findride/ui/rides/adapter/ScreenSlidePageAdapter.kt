package com.example.findride.ui.rides.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.findride.ui.rides.booking.RequestedRidesListFragment
import com.example.findride.ui.rides.booking.UserBookedRidesListFragment

class ScreenSlidePageAdapter(fragmentActivity: Fragment) : FragmentStateAdapter(fragmentActivity) {
  override fun getItemCount(): Int = 2
  
  override fun createFragment(position: Int): Fragment {
    return when (position) {
      0 -> RequestedRidesListFragment()
      1 -> UserBookedRidesListFragment()
      else -> RequestedRidesListFragment()
    }
  }
}