package com.illeyrocci.beautyroute.data.repository

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.illeyrocci.beautyroute.domain.repository.MediaRepository
import kotlinx.coroutines.tasks.await
import java.util.UUID

class MediaRepositoryImpl(private val storage: FirebaseStorage = Firebase.storage) :
    MediaRepository {
    override suspend fun putImage(uri: Uri, path: String) {
        storage.reference.child("$path/SERVICE_IMG${UUID.randomUUID()}").putFile(uri).await()
    }

    override suspend fun getAllUrls(path: String): ArrayList<String> {
        val resList = arrayListOf<String>()
        val itemRefs = storage.getReference(path).listAll().await().items
        itemRefs.forEach { itemRef ->
            if (itemRef.name.startsWith("SERVICE_IMG")) {
                resList.add(itemRef.downloadUrl.await().toString())
            }
        }
        return resList
    }
}