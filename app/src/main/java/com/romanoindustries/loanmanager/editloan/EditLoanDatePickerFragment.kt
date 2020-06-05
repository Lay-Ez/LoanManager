package com.romanoindustries.loanmanager.editloan

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class EditLoanDatePickerFragment(private val passedTime: Long) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        if (passedTime != 0L) {
            calendar.timeInMillis = passedTime
        }
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]

        return DatePickerDialog(activity!!, activity as OnDateSetListener?,
                year, month, day)
    }
}