package com.illeyrocci.beautyroute.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.illeyrocci.beautyroute.R
import com.illeyrocci.beautyroute.databinding.FragmentUserProfileBinding
import com.illeyrocci.beautyroute.presentation.recycler.UserServicesAdapter
import com.illeyrocci.beautyroute.presentation.viewmodel.UserProfileViewModel
import com.illeyrocci.beautyroute.presentation.viewmodel.UserProfileViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val viewModel: UserProfileViewModel by viewModels { UserProfileViewModelFactory() }

    private lateinit var navController: NavController

    private lateinit var adapter: UserServicesAdapter

    private val args: UserProfileFragmentArgs by navArgs<UserProfileFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        navController = findNavController()
        viewModel.setUserId(args.uid)
        val lambda = { servicePosition: Int, duration: Int ->
            //viewModel.makeAppointment(, duration, duration)
            navController.navigate(
                UserProfileFragmentDirections.userProfileToUserSchedule(
                    viewModel.state.value.userId,
                    servicePosition,
                    duration
                )
            )
        }

        adapter = UserServicesAdapter(requireActivity(), lambda)

        _binding = FragmentUserProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().apply {
            findViewById<Toolbar>(R.id.my_toolbar).isVisible = true
            findViewById<BottomNavigationView>(R.id.bottom_navigation).isVisible = false
        }

        binding.apply {
            userServicesList.adapter = adapter
            textAddToFavourites.setOnClickListener { viewModel.addToFavourites(args.uid) }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    binding.apply {
                        nameUserProfile.text = state.name
                        phoneUserProfile.text = state.phone
                        textLocationUserProfile.text = state.address
                        descriptionUserProfile.text = state.description
                        adapter.update(state.services)
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}