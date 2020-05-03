package com.romanoindustries.loanmanager.editloan

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.romanoindustries.loanmanager.R
import com.romanoindustries.loanmanager.databinding.ActivityEditLoanBinding
import com.romanoindustries.loanmanager.datamodel.Loan
import java.text.DateFormat
import java.util.*
import kotlin.math.roundToInt


const val LOAN_ID_KEY = "loan_id_key"
const val TAG = "EditLoanActivity"

class EditLoanActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    lateinit var binding: ActivityEditLoanBinding
    private lateinit var currentlyEditedLoan: Loan
    private lateinit var viewModel: EditLoanViewModel
    private lateinit var interestFragment: EditLoanInterestFragment
    var wholePercentPart: Int = 0
    var decimalPercentPart: Int = 0
    private var periodInDays: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditLoanBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.newLoanToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        interestFragment = EditLoanInterestFragment()
        processIntent()
        setListeners()
        hideErrorsOnInput()
    }

    private fun processIntent() {
        val loanId = intent.getIntExtra(LOAN_ID_KEY, -1)
        if (loanId == -1) {
            onBackPressed()
            return
        }
        viewModel = ViewModelProvider(this).get(EditLoanViewModel::class.java)
        viewModel.allLoans.observe(this, Observer { loans ->
            loans.find { loan -> loan.id == loanId }.also {
                loadFoundLoanToVm(it)
            }})
        viewModel.editedLoan.observe(this, Observer { displayLoan(it) })
        viewModel.wholePercent.observe(this, Observer {
            wholePercentPart = it
            Log.d(TAG, "processIntent: new rate = ${convertInterestRateToDouble(wholePercentPart, decimalPercentPart)}")})
        viewModel.decimalPercent.observe(this, Observer {
            decimalPercentPart = it
            Log.d(TAG, "processIntent: new rate = ${convertInterestRateToDouble(wholePercentPart, decimalPercentPart)}")})
        viewModel.periodInDays.observe(this, Observer {
            periodInDays = it
            Log.d(TAG, "processIntent: period in days = $it")})
    }

    private fun loadFoundLoanToVm(loan: Loan?) {
        if (loan == null) return
        if (!viewModel.loanAlreadyFound) {
            Log.d(TAG, "loadFoundLoanToVm: loans rate = ${loan.interestRate}")
            viewModel.setEditedLoan(loan)
            if (loan.interestRate != 0.0) {
                val wholePart = loan.interestRate.toInt()
                val decimalPart = ((loan.interestRate - wholePart) * 100).roundToInt()
                viewModel.setWholePercent(wholePart)
                viewModel.setDecimalPercent(decimalPart)
                viewModel.setPeriodInDays(loan.periodInDays)
                binding.enableInterestCb.isChecked = true
            } else {
                viewModel.setWholePercent(0)
                viewModel.setDecimalPercent(0)
                viewModel.setPeriodInDays(1)
                binding.enableInterestCb.isChecked = false
            }
            viewModel.loanAlreadyFound = true
        }
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
    }

    private fun setListeners() {
        binding.endDateBtn.setOnClickListener {
            hideKeyboard()
            val datePicker: DialogFragment = EditLoanDatePickerFragment(currentlyEditedLoan.paymentDateInMs)
            datePicker.show(supportFragmentManager, "date_picker")
        }

        binding.noEndDateCb.setOnCheckedChangeListener { _, isChecked ->
            run {
                hideKeyboard()
                binding.endDateBtn.isEnabled = !isChecked
                currentlyEditedLoan.paymentDateInMs = 0
            }
        }

        binding.enableInterestCb.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            hideKeyboard()
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            if (isChecked) {
                fragmentTransaction.replace(R.id.new_loan_fragment_container, interestFragment)
                fragmentTransaction.commit()
            } else {
                fragmentTransaction.remove(interestFragment)
                fragmentTransaction.commit()
            }
        }
    }

    private fun onLoanSavePressed() {
        if (isInputCorrect()) {
            saveInputToCurrentLoan()
            viewModel.updateLoan(currentlyEditedLoan)
            onBackPressed()
        }
    }

    private fun saveInputToCurrentLoan() {
        val name = binding.editTextName.text.toString().trim()
        val phone = binding.editTextPhone.text.toString().trim()
        val amount = binding.editTextAmount.text.toString().toInt()
        val note = binding.editTextNote.text.toString().trim()

        currentlyEditedLoan.debtorName = name
        currentlyEditedLoan.phoneNumber = phone
        currentlyEditedLoan.currentAmount = amount
        currentlyEditedLoan.specialNote = note
    }

    fun showInterestRateError() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.interest_rate_zero_msg)
                .setPositiveButton("OK") { _: DialogInterface?, _: Int -> }
        val dialog = builder.create()
        dialog.show()
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
            currentlyEditedLoan.paymentDateInMs = timeInMillis
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

    override fun onPause() {
        viewModel.setEditedLoan(currentlyEditedLoan)
        super.onPause()
    }

    override fun onBackPressed() {
        stopObservingVm()
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        stopObservingVm()
        return true
    }

    private fun stopObservingVm() {
        viewModel.allLoans.removeObservers(this)
        viewModel.editedLoan.removeObservers(this)
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
