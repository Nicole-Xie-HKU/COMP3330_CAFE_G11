package com.group11.cafe.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.group11.cafe.data.model.User

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

    fun getCurrentUser(
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            onFailure(IllegalStateException("No signed-in user."))
            return
        }

        firestore
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)

                if (user != null) {
                    onSuccess(user)
                } else {
                    onFailure(IllegalStateException("User profile not found."))
                }
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }
}