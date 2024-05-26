package com.illeyrocci.beautyroute.domain.usecase

import android.util.Log
import com.illeyrocci.beautyroute.domain.repository.AuthRepository
import com.illeyrocci.beautyroute.domain.repository.UserRepository
import java.util.Date

class SwitchSlotByPositionUseCase(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        day: Date,
        pos: Int
    ) {
        Log.d("TAGGG", "$day")
        val uid = authRepository.getMyUID().data!!

        val changeUserDataResource =
            userRepository.changeUserScheduleSection(
                uid,
                day,
                pos
            )
    }

}