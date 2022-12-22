package com.example.findride.ui.authentication


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.findride.R
import com.example.findride.common.IMAGE
import com.example.findride.common.makeToast
import com.example.findride.viewmodel.RegistrationViewModel
import com.example.findride.databinding.FragmentRegistrationBinding
import com.example.findride.isNetworkAvailable
import com.example.findride.model.ScreenState
import com.example.findride.onClick
import com.example.findride.subscribe
import com.example.findride.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationFragment : BaseFragment<FragmentRegistrationBinding>() {
  private val viewModel: RegistrationViewModel by viewModels()
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRegistrationBinding
    get() = FragmentRegistrationBinding::inflate
  private val loadImage = registerForActivityResult(
    ActivityResultContracts.GetContent()
  ) {
    binding.apply {
      context?.let { context -> Glide.with(context).load(it).centerCrop().into(profileImage) }
      if (it != null) viewModel.setProfileImage(it.toString())
    }
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
  
  override fun onPostViewCreated() {
    initListeners()
    viewModel.userCredentials.observe(viewLifecycleOwner) {
      if (it) viewModel.register(
        binding.usernameInput.text.toString().trim(), binding.passwordInput.text.toString(), binding
          .nameInput.text
          .toString(),
        binding.surnameInput.text.toString(),
        binding.phoneNumberInput.text.toString(),
        getUserPhoto(),
        context.isNetworkAvailable()
      )
      else makeToast(getString(R.string.empty_fields))
    }
    viewModel.isUserRegistered.observe(viewLifecycleOwner) {
      if (it) navigateToMainScreen()
    }
    viewModel.screenState.subscribe(viewLifecycleOwner, ::observeScreenState)
  }
  
  private fun navigateToMainScreen() {
    findNavController().navigate(R.id.action_registrationFragment_to_mainScreen)
  }
  
  private fun getUserPhoto(): String {
    return viewModel.profilePicture.value.toString()
  }
  
  private fun initListeners() {
    with(binding) {
      registerBtn.onClick {
        viewModel.checkUserCredentials(
          usernameInput.text.toString(), passwordInput.text.toString(), nameInput.text.toString(),
          surnameInput.text.toString(), phoneNumberInput.text.toString()
        )
      }
      btnUpload.onClick {
        loadImage.launch("$IMAGE/*")
      }
    }
  }
}