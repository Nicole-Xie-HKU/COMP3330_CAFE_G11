package com.group11.cafe.utils

import com.google.firebase.auth.FirebaseAuth

object GuestUtils {

    fun isGuest(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        return user == null || user.isAnonymous
    }
}
