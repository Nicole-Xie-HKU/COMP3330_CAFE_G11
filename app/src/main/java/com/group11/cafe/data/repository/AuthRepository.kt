package com.group11.cafe.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.group11.cafe.data.model.User
import com.group11.cafe.data.model.UserRole
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
                val account = registeredAccount(
                    uid = result.user?.uid,
                    name = name,
                    email = email,
                    role = role,
                    inviteCode = UUID
                        .randomUUID()
                        .toString()
                        .replace("-", "")
                        .take(6)
                        .uppercase()
                )

                val user = account.user
                if (user == null) {
                    onFailure(
                        account.error
                            ?: Exception(COULD_NOT_CREATE_ACCOUNT)
                    )
                    return@addOnSuccessListener
                }

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

    fun loginAsGuest(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.signInAnonymously()
            .addOnSuccessListener {
                onSuccess()
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

private const val COULD_NOT_CREATE_ACCOUNT =
    "Could not create the account."

data class RegisteredAccount(
    val user: User?,
    val error: Exception?
)

fun registeredAccount(
    uid: String?,
    name: String,
    email: String,
    role: String,
    inviteCode: String
): RegisteredAccount {
    if (uid == null) {
        return RegisteredAccount(
            user = null,
            error = Exception(COULD_NOT_CREATE_ACCOUNT)
        )
    }

    return RegisteredAccount(
        user = User(
            id = uid,
            name = name,
            email = email,
            role = role,
            inviteCode = if (role == UserRole.ORGANIZER) {
                inviteCode
            } else {
                ""
            },
            participantQrCode = if (role == UserRole.PARTICIPANT) {
                "CAFE-$uid"
            } else {
                ""
            }
        ),
        error = null
    )
}