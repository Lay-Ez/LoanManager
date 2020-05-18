package com.romanoindustries.loanmanager.editloan

import android.text.Editable
import android.text.TextWatcher
import com.romanoindustries.loanmanager.R
import java.util.*

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

fun EditLoanActivity.isInputCorrect(): Boolean {
    var isInputOk = true
    val name = binding.editTextName.text.toString().trim()
    val phone = binding.editTextPhone.text.toString().trim()
    val amountStr = binding.editTextAmount.text.toString()
    val note = binding.editTextNote.text.toString().trim()

    if (name.isBlank()) {
        isInputOk = false
        binding.textInputName.error = getString(R.string.name_cannot_be_empty_error_msg)
    }
    if (amountStr.isBlank()) {
        isInputOk = false
        binding.textInputAmount.error = getString(R.string.amount_should_be_specified_error_msg)
    } else {
        val amount = amountStr.toInt()
        if (amount == 0) {
            isInputOk = false
            binding.textInputAmount.error = getString(R.string.amount_cannot_be_zero_error_msg)
        }
    }
    if (binding.enableInterestCb.isChecked && convertInterestRateToDouble(wholePercentPart, decimalPercentPart) == 0.0) {
        if (isInputOk) {
            showInterestRateError()
        }
        isInputOk = false
    }

    return isInputOk
}

fun EditLoanActivity.convertInterestRateToDouble(whole: Int, decimal: Int): Double {
    return whole.toDouble() + (decimal.toDouble() / 100)
}

fun calculateNextChargingTime(startTime: Long, periodDays: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = startTime
    calendar.add(Calendar.DAY_OF_YEAR, periodDays)
    return calendar.timeInMillis
}

















