package com.kanzankazu.kanzanwidget

import android.view.inputmethod.EditorInfo

enum class ImeAction(val action: Int) {
    IME_ACTION_DONE(EditorInfo.IME_ACTION_DONE),
    IME_ACTION_GO(EditorInfo.IME_ACTION_GO),
    IME_ACTION_NEXT(EditorInfo.IME_ACTION_NEXT),
    IME_ACTION_NONE(EditorInfo.IME_ACTION_NONE),
    IME_ACTION_PREVIOUS(EditorInfo.IME_ACTION_PREVIOUS),
    IME_ACTION_SEARCH(EditorInfo.IME_ACTION_SEARCH),
    IME_ACTION_SEND(EditorInfo.IME_ACTION_SEND),
    IME_ACTION_UNSPECIFIED(EditorInfo.IME_ACTION_UNSPECIFIED),
}
