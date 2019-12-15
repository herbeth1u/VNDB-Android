package com.booboot.vndbandroid.extensions

import android.view.inputmethod.EditorInfo
import android.widget.TextView

fun TextView.preventLineBreak(_maxLines: Int = Int.MAX_VALUE) = apply {
    setSingleLine()
    setHorizontallyScrolling(false)
    maxLines = _maxLines
}

fun TextView.onSubmitListener(action: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId in listOf(EditorInfo.IME_ACTION_GO, EditorInfo.IME_ACTION_DONE)) {
            action()
        }
        false
    }
}