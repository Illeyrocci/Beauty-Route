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
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.illeyrocci.beautyroute.R
import com.illeyrocci.beautyroute.databinding.FragmentAppointmentListBinding
import com.illeyrocci.beautyroute.presentation.recycler.AppointmentAdapter
import com.illeyrocci.beautyroute.presentation.viewmodel.AppointmentListViewModel
import com.illeyrocci.beautyroute.presentation.viewmodel.AppointmentListViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AppointmentListFragment : Fragment() {

    private var _binding: FragmentAppointmentListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val viewModel: AppointmentListViewModel by viewModels { AppointmentListViewModelFactory() }

    private lateinit var adapter: AppointmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        adapter = AppointmentAdapter { id ->
            findNavController().navigate(
                AppointmentListFragmentDirections.appointmentListToAppointment(
                    id
                )
            )
        }
        _binding = FragmentAppointmentListBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().apply {
            findViewById<Toolbar>(R.id.my_toolbar).isVisible = false
            findViewById<BottomNavigationView>(R.id.bottom_navigation).isVisible = true
        }

        binding.appointmentList.adapter = adapter

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    binding.appointmentList.isVisible = state.isNotEmpty()
                    binding.loading.isVisible = state.isEmpty()

                    adapter.update(state)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}