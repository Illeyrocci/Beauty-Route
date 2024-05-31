package com.illeyrocci.beautyroute.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.illeyrocci.beautyroute.R
import com.illeyrocci.beautyroute.databinding.FragmentSearchBinding
import com.illeyrocci.beautyroute.presentation.recycler.SearchAdapter
import com.illeyrocci.beautyroute.presentation.viewmodel.SearchViewModel
import com.illeyrocci.beautyroute.presentation.viewmodel.SearchViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val viewModel: SearchViewModel by viewModels { SearchViewModelFactory(this) }

    private lateinit var searchAdapter: SearchAdapter
    //private lateinit var favouriteAdapter: SearchAdapter

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)

        navController = findNavController()

        val onUserClicked = { uid: String ->
            navController.navigate(SearchFragmentDirections.searchToUserProfile(uid))
        }
        searchAdapter = SearchAdapter(onUserClicked)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().apply {
            findViewById<Toolbar>(R.id.my_toolbar).isVisible = false
            findViewById<BottomNavigationView>(R.id.bottom_navigation).isVisible = true
        }

        val navController = findNavController()

        binding.apply {
            includeNearestAppointment.root.setOnClickListener {
                navController.navigate(
                    SearchFragmentDirections.searchToAppointment(
                        viewModel.state.value.nearestAppointmentId!!
                    )
                )
            }
            searchResults.adapter = searchAdapter
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    searchAdapter.update(state.searchResult)
                    binding.apply {
                        includeNearestAppointment.root.isVisible =
                            state.nearestAppointmentId != null

                        includeFavouriteList.root.isVisible = state.favouriteUsers.isNotEmpty()

                        stubTextSearch.isVisible =
                            state.nearestAppointmentId == null && state.favouriteUsers.isEmpty() && state.searchResult.isEmpty()

                        Log.d("TAGGG", "bebe ${state.searchResult}")

                        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String?): Boolean {
                                Log.d("TAGGG", "query_submitted")
                                includeNearestAppointment.root.isVisible = false

                                includeFavouriteList.root.isVisible = false

                                stubTextSearch.isVisible = false
                                searchResults.isVisible = true
                                if (!query.isNullOrBlank()) viewModel.getSearchResult(query)
                                return true
                            }

                            override fun onQueryTextChange(newText: String?): Boolean {
                                return true
                            }

                        })

                        search.setOnCloseListener {
                            Log.d("TAGGG", "closed")

                            includeNearestAppointment.root.isVisible =
                                state.nearestAppointmentId != null

                            includeFavouriteList.root.isVisible = state.favouriteUsers.isNotEmpty()

                            stubTextSearch.isVisible =
                                state.nearestAppointmentId == null && state.favouriteUsers.isEmpty() && state.searchResult.isEmpty()
                            searchResults.isVisible = false
                            viewModel.clearSearch()
                            return@setOnCloseListener true
                        }
                    }
                    Log.d("TAGGG", "$state")

                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}