package com.kanzankazu.kanzannetwork.response

import com.google.firebase.database.DataSnapshot

sealed class FRDBResult {
    data class Success(val message: String = "", val dataSnapshot: DataSnapshot? = null) : FRDBResult()
    data class Error(val message: String) : FRDBResult()
}
