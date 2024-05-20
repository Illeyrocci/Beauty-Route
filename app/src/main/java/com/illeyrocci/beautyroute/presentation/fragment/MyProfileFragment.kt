package com.illeyrocci.beautyroute.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.illeyrocci.beautyroute.R
import com.illeyrocci.beautyroute.databinding.FragmentMyProfileBinding
import com.illeyrocci.beautyroute.presentation.recycler.MyServicesAdapter
import com.illeyrocci.beautyroute.presentation.viewmodel.MyProfileViewModel
import com.illeyrocci.beautyroute.presentation.viewmodel.MyProfileViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyProfileFragment : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val viewModel: MyProfileViewModel by viewModels { MyProfileViewModelFactory() }

    private val adapter = MyServicesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().apply {
            findViewById<android.widget.Toolbar>(R.id.my_toolbar).isVisible = false
            findViewById<BottomNavigationView>(R.id.bottom_navigation).isVisible = true
        }

        val navController = findNavController()
        binding.apply {
            userServicesList.adapter = adapter
            viewEditProfile.setOnClickListener {
                navController.navigate(MyProfileFragmentDirections.myProfileToEditProfile())
            }
            viewMySchedule.setOnClickListener {
                navController.navigate(MyProfileFragmentDirections.myProfileToMySchedule())
            }
            viewAddService.setOnClickListener {
                viewModel.addNewService()
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    binding.apply {
                        nameMyProfile.text = state.name
                        phoneMyProfile.text = state.phone

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