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

        val datePickerDialog = DatePickerDialog(activity!!, activity as OnDateSetListener?,
                year, month, day)
        val calendarForMinDate = Calendar.getInstance()
        calendarForMinDate.add(Calendar.DAY_OF_YEAR, 1)
        datePickerDialog.datePicker.minDate = calendarForMinDate.timeInMillis
        return datePickerDialog
    }
}