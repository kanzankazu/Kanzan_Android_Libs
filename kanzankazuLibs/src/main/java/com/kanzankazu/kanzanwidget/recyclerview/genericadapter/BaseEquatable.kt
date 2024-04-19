package com.kanzankazu.kanzanwidget.recyclerview.genericadapter

open class BaseEquatable(private val identifier: Int) : Equatable {
    override val uniqueId: Int
        get() = identifier

    override val longId: Long
        get() = 0L
}
