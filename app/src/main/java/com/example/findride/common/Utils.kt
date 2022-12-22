package com.example.findride.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.widget.Toast
import androidx.annotation.DrawableRes
import com.example.findride.App
import com.example.findride.R
import com.example.findride.model.Ride
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


fun makeToast(message: String, lengthLong: Boolean = false) {
  CoroutineScope(Dispatchers.Main).launch {
    Toast.makeText(App.application, message, if (lengthLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
  }
}

fun getRideStartRouteAddress(context: Context, ride: Ride): String {
  val geocoder = Geocoder(context, Locale.getDefault())
  val startAddress: MutableList<Address>? = geocoder?.getFromLocation(
    ride.startDestinationLat.toDouble(), ride.startDestinationLong.toDouble(), 1
  )
  val lastIndexFirst = startAddress!![0].getAddressLine(0).lastIndexOf(",")
  val start = startAddress!![0].getAddressLine(0).substring(0, lastIndexFirst)
  return "$start"
}

fun getRideEndRouteAddress(context: Context, ride: Ride): String {
  val geocoder = Geocoder(context, Locale.getDefault())
  val endAddress: MutableList<Address>? = geocoder?.getFromLocation(
    ride.endDestinationLat.toDouble(), ride.endDestinationLong.toDouble(), 1
  )
  val lastIndexSecond = endAddress!![0].getAddressLine(0).lastIndexOf(",")
  val end = endAddress!![0].getAddressLine(0).substring(0, lastIndexSecond)
  return "$end"
}

fun isDateValid(day: Int, month: Int, year: Int): Boolean {
  val _day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
  val _month = Calendar.getInstance().get(Calendar.MONTH) + 1
  val _year = Calendar.getInstance().get(Calendar.YEAR)
  if (year < _year) return false
  if (month < _month) return false
  else if (year == _year && month == _month && day < _day) return false
  else return true
}

fun showDate(day: Int, month: Int, year: Int): String = "$day.$month.$year."

fun formatTime(seconds: Int): String {
  val totalMinutes = seconds / 60
  var hours = (totalMinutes / 60).toString()
  var minutes = (totalMinutes % 60).toString()
  if (hours.toInt() < 10) hours = "0$hours"
  if (minutes.toInt() < 10) minutes = "0$minutes"
  return "$hours:$minutes"
}


