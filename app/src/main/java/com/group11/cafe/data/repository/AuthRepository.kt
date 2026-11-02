package com.group11.cafe.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.group11.cafe.data.model.User

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
        Log.d("REGISTER", "Firebase register called")

        auth.createUserWithEmailAndPassword(
            email,
            password
        )
            .addOnSuccessListener { result ->
                Log.d("REGISTER", "Firebase auth success")

                val user =
                    User(
                        id = result.user!!.uid,
                        name = name,
                        email = email,
                        role = role
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
                Log.e("REGISTER", "Firebase auth failed: ${it.message}")
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