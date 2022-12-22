package com.example.findride

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

inline fun View.onClick(crossinline onClick: () -> Unit) {
  this.setOnClickListener {
    onClick()
  }
}

fun <T> LiveData<T>.subscribe(lifecycleOwner: LifecycleOwner, action: (T) -> Unit) {
  this.observe(lifecycleOwner, Observer { action(it) })
}

fun Context?.isNetworkAvailable(): Boolean {
  this?.let {
    val connectivityManager = it.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE)
    return if (connectivityManager is ConnectivityManager) {
      val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
      networkInfo?.isConnected ?: false
    } else false
  }
  return false
}