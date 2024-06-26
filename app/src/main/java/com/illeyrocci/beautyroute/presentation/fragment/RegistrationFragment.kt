package com.illeyrocci.beautyroute.presentation.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.illeyrocci.beautyroute.R
import com.illeyrocci.beautyroute.databinding.FragmentRegistrationBinding
import com.illeyrocci.beautyroute.presentation.common.BaseTextWatcher
import com.illeyrocci.beautyroute.presentation.viewmodel.RegistrationStatus
import com.illeyrocci.beautyroute.presentation.viewmodel.RegistrationViewModel
import com.illeyrocci.beautyroute.presentation.viewmodel.RegistrationViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null

    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    val viewModel: RegistrationViewModel by viewModels { RegistrationViewModelFactory(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        requireActivity().apply {
            findViewById<Toolbar>(R.id.my_toolbar).isVisible = true
            findViewById<BottomNavigationView>(R.id.bottom_navigation).isVisible = false
        }

        val navController = findNavController()
        with(binding) {

            val textWatcher = object : BaseTextWatcher() {
                override fun afterTextChanged(s: Editable?) {
                    viewModel.updateFormState(
                        editSetName.text.toString(),
                        editSetPhone.text.toString(),
                        editSetEmail.text.toString(),
                        editSetPassword.text.toString(),
                        editSetCity.text.toString()
                    )
                }
            }
            editSetEmail.addTextChangedListener(textWatcher)
            editSetPhone.addTextChangedListener(textWatcher)
            editSetName.addTextChangedListener(textWatcher)
            editSetPassword.addTextChangedListener(textWatcher)
            editSetCity.addTextChangedListener(textWatcher)

            signUp.setOnClickListener {
                viewModel.signUp()
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->

                    @StringRes val statusMessage = when (state.registrationStatus) {
                        RegistrationStatus.DEFAULT -> R.string.error
                        RegistrationStatus.EMAIL_BAD_FORMAT -> R.string.bad_email
                        RegistrationStatus.WEAK_PASSWORD -> R.string.weak_password
                        RegistrationStatus.SUCCESS -> R.string.user_created
                        RegistrationStatus.LOADING -> R.string.error
                        RegistrationStatus.EMAIL_COLLISION -> R.string.email_collision
                        RegistrationStatus.SOMETHING_WRONG -> R.string.error
                        RegistrationStatus.POOR_NETWORK -> R.string.error_network
                        RegistrationStatus.POOR_WEB -> R.string.error_web
                        RegistrationStatus.TOO_MANY_REQUESTS -> R.string.too_many_requests
                        RegistrationStatus.API_NOT_AVAILABLE -> R.string.api_not_available
                        RegistrationStatus.EMPTY_INPUTS -> R.string.empty_inputs
                    }

                    binding.linearProgressBar.isVisible =
                        state.registrationStatus == RegistrationStatus.LOADING

                    val color = when (state.registrationStatus) {
                        RegistrationStatus.SUCCESS -> Color.GREEN
                        RegistrationStatus.EMAIL_COLLISION -> Color.CYAN
                        else -> Color.MAGENTA
                    }

                    if (state.registrationStatus != RegistrationStatus.LOADING && state.registrationStatus != RegistrationStatus.DEFAULT) {
                        val snackbar =
                            Snackbar.make(binding.root, statusMessage, Snackbar.LENGTH_LONG)
                        snackbar.setTextColor(color)
                        snackbar.show()
                    }

                    if (state.registrationStatus == RegistrationStatus.SUCCESS) {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            SAVED_STATE_CREDENTIALS_KEY,
                            "${binding.editSetEmail.text} ${binding.editSetPassword.text}"
                        )
                        navController.popBackStack()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val SAVED_STATE_CREDENTIALS_KEY = "saved state credentials key"
    }
}