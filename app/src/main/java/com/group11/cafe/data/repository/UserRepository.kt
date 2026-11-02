package com.group11.cafe.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {

    private val auth =
        FirebaseAuth.getInstance()

    private val firestore =
        FirebaseFirestore.getInstance()

    fun getCurrentUserRole(
        onResult: (String) -> Unit
    ) {

        val uid =
            auth.currentUser?.uid ?: return

        firestore
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {

                val role =
                    it.getString("role")
                        ?: "Participant"

                onResult(role)
            }
    }
}