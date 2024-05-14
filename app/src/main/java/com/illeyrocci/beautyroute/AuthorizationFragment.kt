package com.illeyrocci.beautyroute

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.illeyrocci.beautyroute.databinding.FragmentAuthorizationBinding

class AuthorizationFragment : Fragment() {
    private var _binding: FragmentAuthorizationBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

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
        binding.apply {
            signIn.setOnClickListener {
                navController.navigate(AuthorizationFragmentDirections.loginToSearch())
            }
            viewSignUp.setOnClickListener {
                navController.navigate(AuthorizationFragmentDirections.loginToRegistration())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}