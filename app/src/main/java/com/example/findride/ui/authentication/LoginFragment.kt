package com.example.findride.ui.authentication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.findride.*
import com.example.findride.common.makeToast
import com.example.findride.databinding.FragmentLoginBinding
import com.example.findride.model.ScreenState
import com.example.findride.ui.base.BaseFragment
import com.example.findride.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
  private val viewModel: LoginViewModel by viewModels()
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoginBinding
    get() = FragmentLoginBinding::inflate
  
  override fun onPostViewCreated() {
    initListeners()
    viewModel.userCredentials.observe(viewLifecycleOwner) {
      if (it) viewModel.login(binding.usernameInput.text.toString(), binding.passwordInput.text.toString(), context.isNetworkAvailable())
      else makeToast(getString(R.string.empty_fields))
    }
    viewModel.isUserLoggedIn.observe(viewLifecycleOwner) {
      if (it) navigateToMainScreen()
    }
    viewModel.screenState.subscribe(viewLifecycleOwner, ::observeScreenState)
  }
  
  private fun observeScreenState(screenState: ScreenState) {
    when (screenState) {
      ScreenState.LOADING -> {
        binding.progress.root.visibility = View.VISIBLE
      }
      ScreenState.IDLE -> {
        binding.progress.root.visibility = View.GONE
      }
      ScreenState.NO_INTERNET -> {
        binding.progress.root.visibility = View.GONE
        makeToast(getString(R.string.no_internet))
      }
    }
  }
  
  private fun initListeners() {
    with(binding) {
      goToRegistration.onClick {
        navigateToRegistration()
      }
      loginBtn.onClick {
        viewModel.checkUserCredentials(usernameInput.text.toString(), passwordInput.text.toString())
      }
    }
  }
  
  private fun navigateToMainScreen() {
    findNavController().navigate(R.id.action_loginFragment_to_mainScreen)
  }
  
  private fun navigateToRegistration() {
    findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
  }
  
}