package com.illeyrocci.beautyroute.domain.repository

import android.net.Uri

interface MediaRepository {
    suspend fun putImage(uri: Uri, path: String)

    suspend fun getAllUrls(path: String) : ArrayList<String>
}