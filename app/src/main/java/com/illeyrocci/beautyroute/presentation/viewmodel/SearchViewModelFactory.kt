package com.illeyrocci.beautyroute.presentation.viewmodel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.illeyrocci.beautyroute.data.repository.AuthRepositoryImpl
import com.illeyrocci.beautyroute.data.repository.UserRepositoryImpl
import com.illeyrocci.beautyroute.domain.usecase.FindMastersUseCase

class SearchViewModelFactory (
    //TODO("INJECTION REPOS")
    owner: SavedStateRegistryOwner
) :
    AbstractSavedStateViewModelFactory(owner, null) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {

        val authRepository = AuthRepositoryImpl()
        val userRepository = UserRepositoryImpl()

        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(
                FindMastersUseCase(userRepository),
                handle
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}