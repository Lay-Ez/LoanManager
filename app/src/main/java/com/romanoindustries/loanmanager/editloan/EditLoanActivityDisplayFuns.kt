package com.romanoindustries.loanmanager.editloan

import android.text.Editable
import android.text.TextWatcher

fun EditLoanActivity.hideErrorsOnInput() {

    binding.editTextAmount.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            binding.textInputAmount.error = null
        }
        override fun afterTextChanged(s: Editable) {}
    })

    binding.editTextName.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            binding.textInputName.error = null
        }
        override fun afterTextChanged(s: Editable) {}
    })

}