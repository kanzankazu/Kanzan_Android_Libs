package com.kanzankazu.kanzannetwork.response

import com.google.firebase.auth.FirebaseUser

// Sealed class untuk menangani hasil autentikasi
sealed class FAuthResult {
    data class Success(val user: FirebaseUser?) : FAuthResult()
    data class Error(val exception: String) : FAuthResult()
}
