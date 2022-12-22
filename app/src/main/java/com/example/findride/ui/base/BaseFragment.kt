package com.example.findride.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<BindingType : ViewBinding> : Fragment() {
  
  private var _binding: ViewBinding? = null
  abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BindingType
  
  val binding: BindingType
    get() = _binding as BindingType
  
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = bindingInflater(inflater,container,false)
    return binding?.root
    
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    onPostViewCreated()
  }
  
  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }
  
  abstract fun onPostViewCreated()
}
