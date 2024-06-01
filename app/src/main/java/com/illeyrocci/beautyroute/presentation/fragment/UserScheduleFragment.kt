package com.illeyrocci.beautyroute.presentation.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.illeyrocci.beautyroute.R
import com.illeyrocci.beautyroute.databinding.FragmentUserScheduleBinding
import com.illeyrocci.beautyroute.presentation.recycler.UserScheduleAdapter
import com.illeyrocci.beautyroute.presentation.viewmodel.UserScheduleViewModel
import com.illeyrocci.beautyroute.presentation.viewmodel.UserScheduleViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserScheduleFragment : Fragment() {

    private var _binding: FragmentUserScheduleBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val viewModel: UserScheduleViewModel by viewModels { UserScheduleViewModelFactory() }

    private lateinit var adapter: UserScheduleAdapter

    private lateinit var navController: NavController

    private val args by navArgs<UserScheduleFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        navController = findNavController()

        adapter = UserScheduleAdapter(args.duration) { pos: Int ->
            viewModel.makeAppointment(
                args.uid,
                args.servicePosition,
                86400000 + ((6L * 60 + pos * 15) * 60000) % 86400000 + viewModel.getDate().time - (viewModel.getDate().time % 86400000),
                86400000 + ((6L * 60 + pos * 15 + args.duration) * 60000) % 86400000 + viewModel.getDate().time - (viewModel.getDate().time % 86400000)
            )
        }
        requireActivity().findViewById<TextView>(R.id.text_toolbar).text =
            SimpleDateFormat("MMMM, d", Locale.GERMAN).format(viewModel.getDate())

        _binding = FragmentUserScheduleBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ) { _, bundle ->
            val newDate =
                bundle.customGetSerializable<Date>(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            viewModel.setDate(newDate)
            Log.d("TAGGG", "Date we got: $newDate")
            requireActivity().findViewById<TextView>(R.id.text_toolbar).text =
                SimpleDateFormat("MMMM, d", Locale.GERMAN).format(newDate)
        }

        requireActivity().apply {
            findViewById<Toolbar>(R.id.my_toolbar).apply {
                menu.clear()
                inflateMenu(R.menu.action_schedule)
                setOnMenuItemClickListener {
                    if (it.itemId == R.id.edit_schedule) {
                        navController.navigate(
                            UserScheduleFragmentDirections.userScheduleToDatePicker(
                                viewModel.getDate()
                            )
                        )

                    }
                    return@setOnMenuItemClickListener true
                }
                isVisible = true
            }
            findViewById<BottomNavigationView>(R.id.bottom_navigation).isVisible = false
        }

        with(binding) {
            userScheduleGrid.adapter = adapter
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    binding.apply {
                        try {
                            Log.d(
                                "TAGGG",
                                "collected in sch fragment: ${viewModel.getCurrentDayIndex(args.uid)}, state = $state"
                            )
                            adapter.update(state.schedule[viewModel.getCurrentDayIndex(args.uid)])
                        } catch (e: Exception) {
                            Log.d("TAGGG", viewModel.getCurrentDayIndex(args.uid).toString())
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

    @Suppress("DEPRECATION")
    private inline fun <reified T : Serializable> Bundle.customGetSerializable(key: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getSerializable(key, T::class.java)
        } else {
            getSerializable(key) as? T
        }
    }
}