package com.kanzankazu.kanzandatabase.firebase.realtimedatabase

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.handleBaseResponseConvert
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.handleBaseResponseConvertToObject
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.toBaseResponseSuccess
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.toError
import com.kanzankazu.kanzanutil.kanzanextension.isDebug
import com.kanzankazu.kanzanutil.kanzanextension.toObject
import com.kanzankazu.kanzanutil.kanzanextension.toObjectList
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessage
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import java.math.BigInteger
import kotlin.coroutines.resume

open class RealtimeDatabaseImpl : RealtimeDatabase {
    override fun getRootRefKt(tableChildKey: String) =
        FirebaseDatabase.getInstance().getReference(tableChildKey)

    override fun createPrimaryKeyData(tableChildKey: String): String =
        getRootRefKt(tableChildKey).push().key ?: ""

    override fun <T> setDataBaseResponse(
        tableChildKey: String,
        tableChildKeyId: String,
        value: T,
        function: (BaseResponse<String>) -> Unit,
    ) {
        "RealtimeDatabaseImpl - setDataKanzanBaseResponse - $tableChildKey - $tableChildKeyId".debugMessage()
        getRootRefKt(tableChildKey).child(tableChildKeyId).setValue(value)
            .handleTaskBaseResponse(RealtimeDatabaseImplType.SAVE, function)
    }

    override suspend fun <T> setDataBaseResponse(
        tableChildKey: String,
        tableChildKeyId: String,
        value: T,
    ): BaseResponse<String> {
        "RealtimeDatabaseImpl - setDataKanzanBaseResponse - $tableChildKey - $tableChildKeyId".debugMessage()
        return getRootRefKt(tableChildKey).child(tableChildKeyId).setValue(value)
            .handleTaskBaseResponse(RealtimeDatabaseImplType.SAVE)
    }

    override fun <T> updateDataBaseResponse(
        tableChildKey: String,
        tableChildKeyId: String,
        value: T,
        function: (BaseResponse<String>) -> Unit,
    ) {
        "RealtimeDatabaseImpl - updateDataKanzanBaseResponse - $tableChildKey - $tableChildKeyId".debugMessage()
        getRootRefKt(tableChildKey)
            .child(tableChildKeyId)
            .setValue(value)
            .handleTaskBaseResponse(RealtimeDatabaseImplType.UPDATE, function)
    }

    override suspend fun <T> updateDataBaseResponse(
        tableChildKey: String,
        tableChildKeyId: String,
        value: T,
    ): BaseResponse<String> {
        "RealtimeDatabaseImpl - updateDataKanzanBaseResponse - $tableChildKey - $tableChildKeyId".debugMessage()
        return getRootRefKt(tableChildKey).child(tableChildKeyId).setValue(value)
            .handleTaskBaseResponse(RealtimeDatabaseImplType.UPDATE)
    }

    override fun updateDataBaseResponse(
        tableChildKey: String,
        tableChildKeyId: String,
        rowChildKey: String,
        rowChildValue: String,
        function: (BaseResponse<String>) -> Unit,
    ) {
        "RealtimeDatabaseImpl - updateDataKanzanBaseResponse - $tableChildKey - $tableChildKeyId".debugMessage()
        getRootRefKt(tableChildKey).child(tableChildKeyId).child(rowChildKey)
            .setValue(rowChildValue)
            .handleTaskBaseResponse(RealtimeDatabaseImplType.UPDATE, function)
    }

    override suspend fun updateDataBaseResponse(
        tableChildKey: String,
        tableChildKeyId: String,
        rowChildKey: String,
        rowChildValue: String,
    ): BaseResponse<String> {
        "RealtimeDatabaseImpl - updateDataKanzanBaseResponse - $tableChildKey - $tableChildKeyId".debugMessage()
        return getRootRefKt(tableChildKey)
            .child(tableChildKeyId)
            .child(rowChildKey)
            .setValue(rowChildValue)
            .handleTaskBaseResponse(RealtimeDatabaseImplType.UPDATE)
    }

    override fun removeDataBaseResponse(
        tableChildKey: String,
        tableChildKeyId: String,
        function: (BaseResponse<String>) -> Unit,
    ) {
        "RealtimeDatabaseImpl - removeDataKanzanBaseResponse - $tableChildKey - $tableChildKeyId".debugMessage()
        getRootRefKt(tableChildKey).child(tableChildKeyId).removeValue()
            .handleTaskBaseResponse(RealtimeDatabaseImplType.REMOVE, function)
    }

    override suspend fun removeDataBaseResponse(
        tableChildKey: String,
        tableChildKeyId: String,
    ): BaseResponse<String> {
        "RealtimeDatabaseImpl - removeDataKanzanBaseResponse - $tableChildKey - $tableChildKeyId".debugMessage()
        return getRootRefKt(tableChildKey).child(tableChildKeyId).removeValue()
            .handleTaskBaseResponse(RealtimeDatabaseImplType.REMOVE)
    }

    override fun getDataAllBaseResponse(
        tableChildKey: String,
        isSingleCall: Boolean,
        function: (BaseResponse<DataSnapshot>) -> Unit,
    ) {
        "RealtimeDatabaseImpl - getDataAllKanzanBaseResponse - $tableChildKey".debugMessage()
        getRootRefKt(tableChildKey).handleTaskBaseResponse(isSingleCall, function)
    }

    override suspend fun getDataAllBaseResponse(
        tableChildKey: String,
        isSingleCall: Boolean,
    ): BaseResponse<DataSnapshot> {
        "RealtimeDatabaseImpl - getDataAllKanzanBaseResponse - $tableChildKey".debugMessage()
        return getRootRefKt(tableChildKey).handleTaskBaseResponse(isSingleCall)
    }

    override fun <T> getDataByIdBaseResponse(
        tableChildKey: String,
        tableChildKeyId: String,
        targetClass: Class<T>,
        isSingleCall: Boolean,
        function: (BaseResponse<T>) -> Unit,
    ) {
        "RealtimeDatabaseImpl - getDataByIdKanzanBaseResponse - $tableChildKey - $tableChildKeyId".debugMessage()
        val reference = getRootRefKt(tableChildKey).child(tableChildKeyId)
        reference.handleTaskBaseResponse(isSingleCall) { dataSnapshotKanzanBaseResponse ->
            function.invoke(dataSnapshotKanzanBaseResponse.handleBaseResponseConvert {
                it.toObject(targetClass)?.let { t ->
                    BaseResponse.Success(t)
                } ?: kotlin.run {
                    BaseResponse.Error(if (isDebug()) "RealtimeDatabaseImpl - getDataByIdKanzanBaseResponse : Error Convert" else "Error Convert")
                }
            })
        }
    }

    override suspend fun <T> getDataByIdBaseResponse(
        tableChildKey: String,
        tableChildKeyId: String,
        targetClass: Class<T>,
        isSingleCall: Boolean,
    ): BaseResponse<T> {
        "RealtimeDatabaseImpl - getDataByIdKanzanBaseResponse - $tableChildKey - $tableChildKeyId".debugMessage()
        return querySelectTableDataByPrimaryKey(
            tableChildKey,
            tableChildKeyId
        ).handleTaskBaseResponse(isSingleCall)
            .handleBaseResponseConvertToObject(targetClass)
    }

    override suspend fun <T> getDataByIdBaseResponseFlow(
        tableChildKey: String,
        tableChildKeyId: String,
        targetClass: Class<T>,
        isSingleCall: Boolean,
    ): Flow<BaseResponse<T>> {
        "RealtimeDatabaseImpl - getDataByIdKanzanBaseResponseFlow - $tableChildKey - $tableChildKeyId".debugMessage()
        return flow {
            val response = querySelectTableDataByPrimaryKey(
                tableChildKey,
                tableChildKeyId
            ).handleTaskBaseResponse(isSingleCall)
                .handleBaseResponseConvertToObject(targetClass)
            emit(response)
        }.flowOn(Dispatchers.IO).catch {
            emit((it as Exception).toError())
        }
    }

    override fun querySelectTableDataByPrimaryKey(
        tableChildKey: String,
        tableChildPrimaryKeyId: String,
    ): Query {
        "RealtimeDatabaseImpl - querySelectTableDataByPrimaryKey - $tableChildKey - $tableChildPrimaryKeyId".debugMessage()
        return getRootRefKt(tableChildKey).child(tableChildPrimaryKeyId)
    }

    override fun querySelectTableDataByKeyValue(
        tableChildKey: String,
        rowChildKey: String,
        rowChildValue: String,
    ): Query {
        "RealtimeDatabaseImpl - querySelectTableDataByKeyValue - $tableChildKey - $rowChildKey - $rowChildValue".debugMessage()
        return getRootRefKt(tableChildKey).orderByChild(rowChildKey).equalTo(rowChildValue)
    }

    override fun querySelectTableDataByKeyValue(
        tableChildKey: String,
        rowChildKey: String,
        rowChildValue: Int,
    ): Query {
        "RealtimeDatabaseImpl - querySelectTableDataByKeyValue - $tableChildKey - $rowChildKey - $rowChildValue".debugMessage()
        return getRootRefKt(tableChildKey).orderByChild(rowChildKey).equalTo(rowChildValue.toDouble())
    }

    override fun querySelectTableDataByKeyValue(
        tableChildKey: String,
        rowChildKey: String,
        rowChildValue: Double,
    ): Query {
        "RealtimeDatabaseImpl - querySelectTableDataByKeyValue - $tableChildKey - $rowChildKey - $rowChildValue".debugMessage()
        return getRootRefKt(tableChildKey).orderByChild(rowChildKey).equalTo(rowChildValue)
    }

    override fun querySelectTableDataByKeyValue(
        tableChildKey: String,
        rowChildKey: String,
        rowChildValue: Boolean,
    ): Query {
        "RealtimeDatabaseImpl - querySelectTableDataByKeyValue - $tableChildKey - $rowChildKey - $rowChildValue".debugMessage()
        return getRootRefKt(tableChildKey).orderByChild(rowChildKey).equalTo(rowChildValue)
    }

    override fun querySelectTableDataByKeyValue(
        tableChildKey: String,
        rowChildKeySlashValue: String,
    ): Query {
        "RealtimeDatabaseImpl - querySelectTableDataByKeyValue - $tableChildKey - $rowChildKeySlashValue".debugMessage()
        return getRootRefKt(tableChildKey).orderByChild(rowChildKeySlashValue)
    }

    override fun querySelectTableLimit(tableChildKey: String, limit: Int): Query {
        "RealtimeDatabaseImpl - querySelectTableLimit - $tableChildKey - $limit".debugMessage()
        return getRootRefKt(tableChildKey).orderByKey().limitToFirst(limit)
    }

    override fun querySelectTableValueWithStart(
        tableChildKey: String,
        rowChildKey: String,
        string: String,
    ): Query {
        "RealtimeDatabaseImpl - querySelectTableValueWithStart - $tableChildKey - $rowChildKey - $string".debugMessage()
        return getRootRefKt(tableChildKey).orderByChild(rowChildKey).startAt(string)
            .endAt("${string}\uf8ff")
    }

    override fun querySelectTableValueWithMoreLessDigits(
        tableChildKey: String,
        rowChildKey: String,
        digits: Any,
        isMore: Boolean,
    ): Query {
        "RealtimeDatabaseImpl - querySelectTableValueWithMoreLessDigits - $tableChildKey - $rowChildKey - $digits".debugMessage()
        val d = when (digits) {
            is Int -> digits.toDouble()
            is Long -> digits.toDouble()
            is Float -> digits.toDouble()
            is BigInteger -> digits.toDouble()
            is Double -> digits
            else -> 0.0
        }

        return if (isMore) getRootRefKt(tableChildKey).orderByChild(rowChildKey).startAt(d + 1)
        else getRootRefKt(tableChildKey).orderByChild(rowChildKey).endAt(d - 1)
    }

    override fun getDataByQuery(
        query: Query,
        onDataChangedListener: (dataSnapshot: DataSnapshot) -> Unit,
        onCancelledListener: (String) -> Unit,
        isSingleCall: Boolean,
    ) {
        if (isSingleCall) query.addListenerForSingleValueEvent(
            handleTaskListenerDataSnapshot({ onDataChangedListener.invoke(it) }, {
                onCancelledListener.invoke("${it.message}|| ${it.details}||${it.code}")
            })
        ) else query.addValueEventListener(
            handleTaskListenerDataSnapshot({ onDataChangedListener.invoke(it) }, {
                onCancelledListener.invoke("${it.message}|| ${it.details}||${it.code}")
            })
        )
    }

    override fun getDataByQueryKanzanBaseResponse(
        query: Query,
        isSingleCall: Boolean,
        function: (BaseResponse<DataSnapshot>) -> Unit,
    ) {
        if (isSingleCall) query.addListenerForSingleValueEvent(
            handleTaskListenerDataSnapshotBaseResponse(function)
        ) else query.addValueEventListener(
            handleTaskListenerDataSnapshotBaseResponse(function)
        )
    }

    override suspend fun getDataByQueryKanzanBaseResponse(
        query: Query,
        isSingleCall: Boolean,
    ): BaseResponse<DataSnapshot> {
        return suspendCancellableCoroutine { continuation ->
            if (isSingleCall) query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    continuation.resume(BaseResponse.Success(dataSnapshot))
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    "getDataByQueryKanzanBaseResponse - addListenerForSingleValueEvent - ${databaseError.message}|${databaseError.details}|${databaseError.code}".debugMessageError()
                    continuation.resume(BaseResponse.Error(databaseError.message))
                }
            }) else query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    continuation.resume(BaseResponse.Success(dataSnapshot))
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    "getDataByQueryKanzanBaseResponse - addListenerForSingleValueEvent - ${databaseError.message}|${databaseError.details}|${databaseError.code}".debugMessageError()
                    continuation.resume(BaseResponse.Error(databaseError.message))
                }
            })
        }
    }

    override suspend fun isExistData(
        tableChildKey: String,
        rowChildKey: String,
        rowChildValue: String,
    ): Pair<Boolean, String> {
        "RealtimeDatabaseImpl - isExistData - $tableChildKey - $rowChildKey - $rowChildValue".debugMessage()
        return suspendCancellableCoroutine {
            querySelectTableDataByKeyValue(
                tableChildKey, rowChildKey, rowChildValue
            ).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    "RealtimeDatabaseImpl - isExistData - onDataChange - $dataSnapshot".debugMessage()
                    it.resume(Pair(dataSnapshot.exists(), ""))
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    "RealtimeDatabaseImpl - isExistData - onCancelled - ${databaseError.message}|${databaseError.details}|${databaseError.code}".debugMessageError()
                    it.resume(Pair(false, "Anda sudah mengirim pengingat hari ini"))
                }
            })

            it.invokeOnCancellation { throwable ->
                "RealtimeDatabaseImpl - isExistData - invokeOnCancellation - ${throwable?.stackTraceToString()}".debugMessageError()
            }
        }
    }

    override fun isExistData(
        tableChildKey: String,
        tableChildPrimaryKeyId: String,
        function: (pair: Pair<Boolean, String>) -> Unit,
    ) {
        "RealtimeDatabaseImpl - isExistData - $tableChildKey - $tableChildPrimaryKeyId".debugMessage()
        getRootRefKt(tableChildKey).child(tableChildPrimaryKeyId)
            .addListenerForSingleValueEvent(handleTaskListenerIsExist(function))
    }

    private fun Task<Void>.handleTask(function: (pair: Pair<Boolean, String>) -> Unit) {
        this.addOnSuccessListener { function.invoke(Pair(true, "")) }
            .addOnCompleteListener { function.invoke(Pair(true, "")) }
            .addOnFailureListener { it.message?.let { s -> function.invoke(Pair(false, s)) } }
            .addOnCanceledListener { function.invoke(Pair(false, "Task Canceled")) }
    }

    private fun Task<Void>.handleTaskBaseResponse(
        realtimeDatabaseImplType: RealtimeDatabaseImplType,
        function: (BaseResponse<String>) -> Unit,
    ) {
        this.addOnSuccessListener {
            function.invoke(
                BaseResponse.Success(
                    handleTaskKanzanBaseResponseMessageSuccess(realtimeDatabaseImplType)
                )
            )
        }.addOnCompleteListener {
            function.invoke(
                BaseResponse.Success(
                    handleTaskKanzanBaseResponseMessageSuccess(realtimeDatabaseImplType)
                )
            )
        }.addOnCanceledListener {
            function.invoke(
                BaseResponse.Error(
                    handleTaskKanzanBaseResponseMessageError(realtimeDatabaseImplType)
                )
            )
        }.addOnFailureListener { exception ->
            exception.message?.let {
                exception.message.toString().debugMessage()
                function.invoke(
                    BaseResponse.Error(
                        handleTaskKanzanBaseResponseMessageError(
                            realtimeDatabaseImplType, exception.message.toString()
                        )
                    )
                )
            } ?: kotlin.run {
                function.invoke(
                    BaseResponse.Error(
                        handleTaskKanzanBaseResponseMessageError(realtimeDatabaseImplType)
                    )
                )
            }
        }
    }

    private suspend fun Task<Void>.handleTaskBaseResponse(
        realtimeDatabaseImplType: RealtimeDatabaseImplType,
    ): BaseResponse<String> {
        return suspendCancellableCoroutine { continuation ->
            addOnSuccessListener {
                continuation.resume(
                    BaseResponse.Success(
                        handleTaskKanzanBaseResponseMessageSuccess(realtimeDatabaseImplType)
                    )
                )
            }
            addOnCompleteListener {
                continuation.resume(
                    handleTaskKanzanBaseResponseMessageSuccess(realtimeDatabaseImplType).toBaseResponseSuccess()
                )
            }
            addOnCanceledListener {
                continuation.resume(
                    BaseResponse.Error(
                        handleTaskKanzanBaseResponseMessageError(realtimeDatabaseImplType)
                    )
                )
            }
            addOnFailureListener { exception ->
                exception.message?.let {
                    exception.message.toString().debugMessage()
                    continuation.resume(
                        BaseResponse.Error(
                            handleTaskKanzanBaseResponseMessageError(
                                realtimeDatabaseImplType, exception.message.toString()
                            )
                        )
                    )
                } ?: kotlin.run {
                    continuation.resume(
                        BaseResponse.Error(
                            handleTaskKanzanBaseResponseMessageError(realtimeDatabaseImplType)
                        )
                    )
                }
            }

            // Tindakan yang akan diambil saat fungsi suspend dibatalkan
            continuation.invokeOnCancellation {
                // Misalnya, batalkan operasi Task jika masih berjalan
                /*if (!isComplete) {
                    cancel()
                }*/
            }
        }
    }

    private fun handleTaskKanzanBaseResponseMessageSuccess(realtimeDatabaseImplType: RealtimeDatabaseImplType): String {
        return when (realtimeDatabaseImplType) {
            RealtimeDatabaseImplType.SAVE -> "Data Berhasil Di Simpan"
            RealtimeDatabaseImplType.UPDATE -> "Data Berhasil Di Ubah"
            RealtimeDatabaseImplType.REMOVE -> "Data Berhasil Di Hapus"
        }
    }

    private fun handleTaskKanzanBaseResponseMessageError(
        realtimeDatabaseImplType: RealtimeDatabaseImplType,
        otherMessage: String = "",
    ): String {
        return otherMessage.ifEmpty {
            when (realtimeDatabaseImplType) {
                RealtimeDatabaseImplType.SAVE -> "Data Gagal Di Simpan"
                RealtimeDatabaseImplType.UPDATE -> "Data Gagal Di Ubah"
                RealtimeDatabaseImplType.REMOVE -> "Data Gagal Di Hapus"
            }
        }
    }

    private fun <T> DatabaseReference.handleTask(
        targetClass: Class<T>,
        isList: Boolean,
        isSingleCall: Boolean,
        function: (Triple<ArrayList<T>?, T?, String>) -> Unit,
    ) {
        if (isSingleCall) this.addListenerForSingleValueEvent(handleTaskListener(targetClass, isList, function))
        else addValueEventListener(handleTaskListener(targetClass, isList, function))
    }

    private fun DatabaseReference.handleTaskBaseResponse(
        isSingleCall: Boolean,
        function: (BaseResponse<DataSnapshot>) -> Unit,
    ) {
        if (isSingleCall) this.addListenerForSingleValueEvent(handleTaskListenerBaseResponse(function))
        else addValueEventListener(handleTaskListenerBaseResponse(function))
    }

    private suspend fun DatabaseReference.handleTaskBaseResponse(
        isSingleCall: Boolean,
    ): BaseResponse<DataSnapshot> {
        "handleTaskKanzanBaseResponse".debugMessage()
        return suspendCancellableCoroutine { continuation ->
            if (isSingleCall) this.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    continuation.resume(BaseResponse.Success(dataSnapshot))
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    "handleTaskListenerKanzanBaseResponse - ${databaseError.message}|${databaseError.details}|${databaseError.code}".debugMessageError()
                    continuation.resume(BaseResponse.Error("${databaseError.message}|${databaseError.details}|${databaseError.code}"))
                }
            })
            else addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    continuation.resume(BaseResponse.Success(dataSnapshot))
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    "handleTaskListenerKanzanBaseResponse - ${databaseError.message}|${databaseError.details}|${databaseError.code}".debugMessageError()
                    continuation.resume(BaseResponse.Error("${databaseError.message}|${databaseError.details}|${databaseError.code}"))
                }
            })
        }
    }

    private suspend fun Query.handleTaskBaseResponse(
        isSingleCall: Boolean,
    ): BaseResponse<DataSnapshot> {
        return suspendCancellableCoroutine { continuation ->
            if (isSingleCall) this.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    continuation.resume(BaseResponse.Success(dataSnapshot))
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    "handleTaskListenerKanzanBaseResponse - ${databaseError.message}|${databaseError.details}|${databaseError.code}".debugMessageError()
                    continuation.resume(BaseResponse.Error("${databaseError.message}|${databaseError.details}|${databaseError.code}"))
                }
            })
            else addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    continuation.resume(BaseResponse.Success(dataSnapshot))
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    "handleTaskListenerKanzanBaseResponse - ${databaseError.message}|${databaseError.details}|${databaseError.code}".debugMessageError()
                    continuation.resume(BaseResponse.Error("${databaseError.message}|${databaseError.details}|${databaseError.code}"))
                }
            })
        }
    }

    private fun <T> Query.handleTask(
        targetClass: Class<T>,
        isList: Boolean,
        isSingleCall: Boolean,
        function: (Triple<ArrayList<T>?, T?, String>) -> Unit,
    ) {
        if (isSingleCall) this.addListenerForSingleValueEvent(
            handleTaskListener(
                targetClass, isList, function
            )
        )
        else addValueEventListener(handleTaskListener(targetClass, isList, function))
    }

    private fun <T> handleTaskListener(
        targetClass: Class<T>,
        isList: Boolean,
        function: (Triple<ArrayList<T>?, T?, String>) -> Unit,
    ): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (isList) function.invoke(
                    Triple(
                        dataSnapshot.toObjectList(targetClass), null, ""
                    )
                )
                else function.invoke(Triple(null, dataSnapshot.toObject(targetClass), ""))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                function.invoke(
                    Triple(
                        null,
                        null,
                        "${databaseError.message}|${databaseError.details}|${databaseError.code}"
                    )
                )
            }
        }
    }

    private fun handleTaskListenerBaseResponse(function: (BaseResponse<DataSnapshot>) -> Unit): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                function.invoke(BaseResponse.Success(dataSnapshot))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                "handleTaskListenerKanzanBaseResponse - ${databaseError.message}|${databaseError.details}|${databaseError.code}".debugMessageError()
                function.invoke(BaseResponse.Error("${databaseError.message}|${databaseError.details}|${databaseError.code}"))
            }
        }
    }

    private fun handleTaskListenerIsExist(function: (pair: Pair<Boolean, String>) -> Unit) =
        object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                function.invoke(Pair(dataSnapshot.exists(), ""))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                "handleTaskListenerIsExist - ${databaseError.message}|${databaseError.details}|${databaseError.code}".debugMessageError()
                function.invoke(Pair(false, databaseError.message))
            }
        }

    private fun handleTaskListenerDataSnapshot(
        onDataChange: (DataSnapshot) -> Unit,
        onCancelled: (DatabaseError) -> Unit,
    ) = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            onDataChange.invoke(dataSnapshot)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            "handleTaskListenerDataSnapshot - ${databaseError.message}|${databaseError.details}|${databaseError.code}".debugMessageError()
            onCancelled.invoke(databaseError)
        }
    }

    private fun handleTaskListenerDataSnapshotBaseResponse(
        function: (BaseResponse<DataSnapshot>) -> Unit,
    ) = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            function.invoke(BaseResponse.Success(dataSnapshot))
        }

        override fun onCancelled(databaseError: DatabaseError) {
            function.invoke(BaseResponse.Error(databaseError.message))
        }
    }
}
