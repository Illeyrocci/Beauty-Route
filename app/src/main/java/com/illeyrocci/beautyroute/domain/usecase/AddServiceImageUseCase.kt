package com.illeyrocci.beautyroute.domain.usecase

import android.net.Uri
import com.illeyrocci.beautyroute.domain.repository.AuthRepository
import com.illeyrocci.beautyroute.domain.repository.MediaRepository
import com.illeyrocci.beautyroute.domain.repository.UserRepository

class AddServiceImageUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(position: Int, uri: Uri) {
        val uid = authRepository.getMyUID().data!!
        val services = userRepository.getUserDataSnapshot(uid)!!.services
        val path =
            "/service_photo/${uid}/${services[position].uid}}"

        mediaRepository.putImage(uri, path)

        userRepository.updateServicePhotos(position, mediaRepository.getAllUrls(path), uid)
    }
}