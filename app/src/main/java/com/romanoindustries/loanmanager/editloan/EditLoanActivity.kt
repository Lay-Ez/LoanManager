package com.romanoindustries.loanmanager.editloan

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.romanoindustries.loanmanager.R
import com.romanoindustries.loanmanager.databinding.ActivityEditLoanBinding
import com.romanoindustries.loanmanager.datamodel.Loan
import com.romanoindustries.loanmanager.newloan.DatePickerFragment
import java.text.DateFormat
import java.util.*


const val LOAN_ID_KEY = "loan_id_key"
const val TAG = "EditLoanActivity"

class EditLoanActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    lateinit var binding: ActivityEditLoanBinding
    private lateinit var currentlyEditedLoan: Loan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditLoanBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.newLoanToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        processIntent()

    }

    private fun processIntent() {
        val loanId = intent.getIntExtra(LOAN_ID_KEY, -1)
        if (loanId == -1) {
            onBackPressed()
            return
        }
        val viewModel: EditLoanViewModel = ViewModelProvider(this).get(EditLoanViewModel::class.java)
        viewModel.allLoans.observe(this, Observer { loans ->
            loans.find { loan -> loan.id == loanId }.also {viewModel.setEditedLoan(loan = it)}})
        viewModel.editedLoan.observe(this, Observer { loan -> displayLoan(loan) })
    }

    private fun displayLoan(loan: Loan?) {
        if (loan == null) return
        currentlyEditedLoan = loan
        binding.editTextName.setText(loan.debtorName)
        binding.editTextPhone.setText(loan.phoneNumber)
        binding.editTextAmount.setText(loan.currentAmount.toString())
        binding.editTextNote.setText(loan.specialNote)

        if (loan.paymentDateInMs != 0L) {
            with(Calendar.getInstance()) {
                timeInMillis = loan.paymentDateInMs
                binding.endDateBtn.text = DateFormat.getDateInstance().format(time)
            }
        } else {
            binding.endDateBtn.isEnabled = false
            binding.noEndDateCb.isChecked = true
        }

        binding.endDateBtn.setOnClickListener {
            hideKeyboard()
            val datePicker: DialogFragment = DatePickerFragment()
            datePicker.show(supportFragmentManager, "date_picker")
        }
    }

    private fun onLoanSavePressed() {
        Log.d(TAG, "onLoanSavePressed")
    }

    private fun onCancelLoanPressed() {
        Log.d(TAG, "onLoanCancelPressed")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        with(Calendar.getInstance()) {
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
            binding.endDateBtn.text = DateFormat.getDateInstance().format(time)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_loan_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.mnu_item_save -> {
                onLoanSavePressed()
                return true
            }
            R.id.mnu_item_cancel -> {
                onCancelLoanPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
