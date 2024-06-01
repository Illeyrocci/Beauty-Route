package com.illeyrocci.beautyroute.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.illeyrocci.beautyroute.R
import com.illeyrocci.beautyroute.databinding.FragmentAppointmentBinding
import com.illeyrocci.beautyroute.presentation.viewmodel.AppointmentViewModel
import com.illeyrocci.beautyroute.presentation.viewmodel.AppointmentViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AppointmentFragment : Fragment() {

    private var _binding: FragmentAppointmentBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val args by navArgs<AppointmentFragmentArgs>()

    private val viewModel: AppointmentViewModel by viewModels { AppointmentViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setAppointment(args.appointmentId)
        requireActivity().apply {
            findViewById<Toolbar>(R.id.my_toolbar).isVisible = true
            findViewById<BottomNavigationView>(R.id.bottom_navigation).isVisible = false
        }

        val navController = findNavController()
        binding.apply {
            viewMasterAppointment.setOnClickListener {
                navController.navigate(AppointmentFragmentDirections.appointmentToUserProfile(""))
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    requireActivity().findViewById<TextView>(R.id.text_toolbar).text = state.first.service.name
                    binding.apply {
                        nameMasterAppointment.text = state.second.name
                        viewMasterAppointment.setOnClickListener {
                            navController.navigate(
                                AppointmentFragmentDirections.appointmentToUserProfile(
                                    state.second.uid
                                )
                            )
                        }

                        textCostAppointment.text = state.first.service.price.toString()

                        textDurationAppointment.text =
                            "${
                                SimpleDateFormat(
                                    "d MMMM",
                                    Locale("ru")
                                ).format(state.first.startTime)
                            }, ${
                                SimpleDateFormat(
                                    "HH:mm",
                                    Locale("ru")
                                ).format(state.first.startTime)
                            } - ${
                                SimpleDateFormat(
                                    "HH:mm",
                                    Locale("ru")
                                ).format(state.first.endTime)
                            }"
                        textLocationAppointment.text = state.second.address

                        cancel.setOnClickListener {
                            viewModel.deleteAppointment()
                            navController.navigate(AppointmentFragmentDirections.appointmentToAppointmentList())
                        }
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