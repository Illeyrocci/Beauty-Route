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
import com.illeyrocci.beautyroute.databinding.FragmentAuthorizationBinding
import com.illeyrocci.beautyroute.presentation.common.BaseTextWatcher
import com.illeyrocci.beautyroute.presentation.viewmodel.AuthStatus
import com.illeyrocci.beautyroute.presentation.viewmodel.AuthorizationViewModel
import com.illeyrocci.beautyroute.presentation.viewmodel.AuthorizationViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AuthorizationFragment : Fragment() {
    private var _binding: FragmentAuthorizationBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    val viewModel: AuthorizationViewModel by viewModels { AuthorizationViewModelFactory(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthorizationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().apply {
            findViewById<Toolbar>(R.id.my_toolbar).isVisible = false
            findViewById<BottomNavigationView>(R.id.bottom_navigation).isVisible = false
        }

        val navController = findNavController()

        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")
            ?.observe(viewLifecycleOwner) {

            }

        with(binding) {

            navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>(
                RegistrationFragment.SAVED_STATE_CREDENTIALS_KEY
            )?.observe(viewLifecycleOwner) {
                val (email, password) = parseRegistrationCredentials(it)
                with(binding) {
                    editEmail.text = Editable.Factory.getInstance().newEditable(email)
                    editPassword.text = Editable.Factory.getInstance().newEditable(password)
                }
            }

            viewSignUp.setOnClickListener {
                navController.navigate(AuthorizationFragmentDirections.loginToRegistration())
            }

            val textWatcher = object : BaseTextWatcher() {
                override fun afterTextChanged(s: Editable?) {
                    viewModel.updateFormState(
                        editEmail.text.toString(),
                        editPassword.text.toString()
                    )
                }
            }
            editEmail.addTextChangedListener(textWatcher)
            editPassword.addTextChangedListener(textWatcher)

            signIn.setOnClickListener {
                viewModel.signIn()
            }

            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->

                        @StringRes val statusMessage = when (state.authStatus) {
                            AuthStatus.DEFAULT -> R.string.error
                            AuthStatus.EMAIL_SENT -> R.string.email_sent
                            AuthStatus.EMAIL_BAD_FORMAT -> R.string.bad_email
                            AuthStatus.WRONG_PASSWORD -> R.string.wrong_password
                            AuthStatus.SUCCESS -> R.string.user_created
                            AuthStatus.LOADING -> R.string.error
                            AuthStatus.SOMETHING_WRONG -> R.string.error
                            AuthStatus.POOR_NETWORK -> R.string.error_network
                            AuthStatus.POOR_WEB -> R.string.error_web
                            AuthStatus.TOO_MANY_REQUESTS -> R.string.too_many_requests
                            AuthStatus.API_NOT_AVAILABLE -> R.string.api_not_available
                            AuthStatus.EMPTY_INPUTS -> R.string.empty_inputs
                            AuthStatus.USER_NOT_FOUND -> R.string.user_not_found
                            AuthStatus.WRONG_CREDENTIALS -> R.string.wrong_credentials
                            AuthStatus.EMAIL_TOO_MANY_REQUESTS -> R.string.email_too_many_requests
                        }

                        linearProgressBar2.isVisible =
                            state.authStatus == AuthStatus.LOADING

                        val color = when (state.authStatus) {
                            AuthStatus.SUCCESS -> Color.GREEN
                            AuthStatus.EMAIL_SENT -> Color.CYAN
                            AuthStatus.EMAIL_TOO_MANY_REQUESTS -> Color.CYAN
                            else -> Color.MAGENTA
                        }

                        if (state.authStatus != AuthStatus.LOADING
                            && state.authStatus != AuthStatus.DEFAULT
                            && state.authStatus != AuthStatus.SUCCESS
                        ) {
                            val snackbar =
                                Snackbar.make(binding.root, statusMessage, Snackbar.LENGTH_LONG)
                            snackbar.setTextColor(color)
                            snackbar.setTextMaxLines(4)
                            snackbar.show()
                        }

                        if (state.authStatus == AuthStatus.SUCCESS) {
                            navController.navigate(AuthorizationFragmentDirections.loginToSearch())
                        }
                    }
                }
            }
        }
    }

    private fun parseRegistrationCredentials(s: String): Array<String> {
        return s.split(" ").toTypedArray()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}