package com.kanzankazu.kanzanutil.kanzanextension

fun Any.currentMethodName() = this::class.java.enclosingMethod?.name
