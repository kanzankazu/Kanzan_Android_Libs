package com.kanzankazu.kanzandatabase.firebase.realtimedatabase

enum class RealtimeDatabaseImplType(val action: String) {
    SAVE("Save"),
    UPDATE("Update"),
    REMOVE("Remove")
}