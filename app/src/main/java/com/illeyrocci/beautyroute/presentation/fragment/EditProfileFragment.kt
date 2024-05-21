package com.illeyrocci.beautyroute.presentation.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.util.Log
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
import com.illeyrocci.beautyroute.databinding.FragmentEditProfileBinding
import com.illeyrocci.beautyroute.presentation.common.BaseTextWatcher
import com.illeyrocci.beautyroute.presentation.viewmodel.EditProfileStatus
import com.illeyrocci.beautyroute.presentation.viewmodel.EditProfileViewModel
import com.illeyrocci.beautyroute.presentation.viewmodel.EditProfileViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment(), View.OnFocusChangeListener {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val viewModel: EditProfileViewModel by viewModels { EditProfileViewModelFactory(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().apply {
            findViewById<Toolbar>(R.id.my_toolbar).apply {
                menu.clear()
                inflateMenu(R.menu.action_profile_edit)
                setOnMenuItemClickListener {
                    if (it.itemId == R.id.save_profile_edit) {
                        viewModel.submitChanges()
                    }
                    return@setOnMenuItemClickListener true
                }
                isVisible = true
            }
            findViewById<BottomNavigationView>(R.id.bottom_navigation).isVisible = false
        }

        with(binding) {

            val textWatcher = object : BaseTextWatcher() {}

            editSetEmail.onFocusChangeListener = this@EditProfileFragment
            editSetName.addTextChangedListener(textWatcher)
            editDescription.onFocusChangeListener = this@EditProfileFragment
            editDescription.addTextChangedListener(textWatcher)
            editSetPhone.onFocusChangeListener = this@EditProfileFragment
            editSetPhone.addTextChangedListener(textWatcher)
            editSetName.onFocusChangeListener = this@EditProfileFragment
            editSetName.addTextChangedListener(textWatcher)
            editSetEmail.onFocusChangeListener = this@EditProfileFragment
            editSetEmail.addTextChangedListener(textWatcher)
            editSetPassword.onFocusChangeListener = this@EditProfileFragment
            editLastPassword.addTextChangedListener(textWatcher)
            editSetCity.onFocusChangeListener = this@EditProfileFragment
            editSetCity.addTextChangedListener(textWatcher)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->

                    @StringRes val statusMessage = when (state.editProfileStatus) {
                        EditProfileStatus.DEFAULT -> R.string.error
                        EditProfileStatus.EMAIL_BAD_FORMAT -> R.string.bad_email
                        EditProfileStatus.WEAK_PASSWORD -> R.string.weak_password
                        EditProfileStatus.SUCCESS -> R.string.user_created
                        EditProfileStatus.LOADING -> R.string.error
                        EditProfileStatus.EMAIL_COLLISION -> R.string.email_collision
                        EditProfileStatus.SOMETHING_WRONG -> R.string.error
                        EditProfileStatus.POOR_NETWORK -> R.string.error_network
                        EditProfileStatus.POOR_WEB -> R.string.error_web
                        EditProfileStatus.TOO_MANY_REQUESTS -> R.string.too_many_requests
                        EditProfileStatus.API_NOT_AVAILABLE -> R.string.api_not_available
                        EditProfileStatus.EMPTY_INPUTS -> R.string.empty_inputs
                    }

                    val color = when (state.editProfileStatus) {
                        EditProfileStatus.SUCCESS -> Color.GREEN
                        EditProfileStatus.EMAIL_COLLISION -> Color.CYAN
                        else -> Color.MAGENTA
                    }

                    if (state.editProfileStatus != EditProfileStatus.LOADING && state.editProfileStatus != EditProfileStatus.DEFAULT) {
                        val snackbar =
                            Snackbar.make(binding.root, statusMessage, Snackbar.LENGTH_LONG)
                        snackbar.setTextColor(color)
                        snackbar.show()
                    }

                    if (state.editProfileStatus == EditProfileStatus.SUCCESS) {
                        findNavController().popBackStack()
                    }
                    binding.apply {
                        editSetEmail.text = Editable.Factory.getInstance().newEditable(state.email)
                        editDescription.text =
                            Editable.Factory.getInstance().newEditable(state.description)
                        editSetPhone.text = Editable.Factory.getInstance().newEditable(state.phone)
                        editSetName.text = Editable.Factory.getInstance().newEditable(state.name)
                        editSetPassword.text =
                            Editable.Factory.getInstance().newEditable(state.password)
                        editLastPassword.text =
                            Editable.Factory.getInstance().newEditable(state.lastPassword)
                        editSetCity.text = Editable.Factory.getInstance().newEditable(state.address)
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        with(binding) {
            viewModel.updateFormState(
                editDescription.text.toString(),
                editSetName.text.toString(),
                editSetPhone.text.toString(),
                editSetEmail.text.toString(),
                editLastPassword.text.toString(),
                editSetPassword.text.toString(),
                editSetCity.text.toString()
            )
        }
        Log.d("TAGGG", "FOCUES CHAGNED")
    }
}