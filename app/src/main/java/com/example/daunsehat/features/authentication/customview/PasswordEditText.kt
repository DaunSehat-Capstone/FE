package com.example.daunsehat.features.authentication.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.example.daunsehat.R
import com.google.android.material.textfield.TextInputLayout

class PasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable) {
                if (hasFocus()) validatePassword()
            }
        })
    }

    private fun validatePassword() {
        val passwordText = text.toString()
        if (passwordText.isBlank() || passwordText.length < 8) {
            (parent.parent as? TextInputLayout)?.error = context.getString(R.string.invalid_password_error)
        } else {
            (parent.parent as? TextInputLayout)?.error = null
        }
    }
}