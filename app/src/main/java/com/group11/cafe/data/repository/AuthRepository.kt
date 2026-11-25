package com.group11.cafe.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.group11.cafe.data.model.User
import java.util.UUID

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore =
        FirebaseFirestore.getInstance()

    fun register(
        name: String,
        email: String,
        password: String,
        role: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(
            email,
            password
        )
            .addOnSuccessListener { result ->
                val user =
                    User(
                        id = result.user!!.uid,
                        name = name,
                        email = email,
                        role = role,
                        inviteCode = if (role == "Organizer") {
                            UUID
                                .randomUUID()
                                .toString()
                                .replace("-", "")
                                .take(6)
                                .uppercase()
                        } else {
                            ""
                        },
                        participantQrCode = if (role == "Participant") {
                            "CAFE-${result.user!!.uid}"
                        } else {
                            ""
                        }
                    )

                firestore
                    .collection("users")
                    .document(user.id)
                    .set(user)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener {
                        onFailure(it)
                    }
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        auth.signInWithEmailAndPassword(
            email,
            password
        )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }
}