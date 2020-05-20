package com.romanoindustries.loanmanager.editloan

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.romanoindustries.loanmanager.R
import com.romanoindustries.loanmanager.databinding.ActivityEditLoanBinding
import com.romanoindustries.loanmanager.datamodel.Loan
import com.romanoindustries.loanmanager.newloan.NewLoanVmHelper
import java.text.DateFormat
import java.util.*
import kotlin.math.roundToInt


const val LOAN_ID_KEY = "loan_id_key"

class EditLoanActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    lateinit var binding: ActivityEditLoanBinding
    private lateinit var currentlyEditedLoan: Loan
    private lateinit var viewModel: EditLoanViewModel
    private lateinit var interestFragment: EditLoanInterestFragment
    private lateinit var datePicker: DialogFragment
    private lateinit var zeroRateErrorDialog: AlertDialog
    private lateinit var confirmCancelDialog: AlertDialog

    var wholePercentPart: Int = 0
    var decimalPercentPart: Int = 0
    private var periodInDays: Int = 1

    private var initialYPositionOfInterestFragment: Float = 0.0f

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
        buildDialogs()
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
            wholePercentPart = it })
        viewModel.decimalPercent.observe(this, Observer {
            decimalPercentPart = it })
        viewModel.periodInDays.observe(this, Observer {
            periodInDays = it })
    }

    private fun loadFoundLoanToVm(loan: Loan?) {
        if (loan == null) return
        if (!viewModel.loanAlreadyFound) {
            viewModel.setEditedLoan(loan)
            if (loan.interestRate != 0.0) {
                val wholePart = loan.interestRate.toInt()
                val decimalPart = ((loan.interestRate - wholePart) * 100).roundToInt()
                val periodInDays = loan.periodInDays
                viewModel.initialWholePercentPart = wholePart
                viewModel.initialDecimalPercentPart = decimalPart
                viewModel.initialPeriodInDays = periodInDays
                viewModel.setWholePercent(wholePart)
                viewModel.setDecimalPercent(decimalPart)
                viewModel.setPeriodInDays(periodInDays)
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
        datePicker = EditLoanDatePickerFragment(currentlyEditedLoan.paymentDateInMs)
    }

    private fun setListeners() {

        binding.textInputName.setEndIconOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivityForResult(intent, Activity.RESULT_FIRST_USER) }

        binding.endDateBtn.setOnClickListener {
            hideKeyboard()
            if (!datePicker.isAdded) {
                datePicker.show(supportFragmentManager, "date_picker")
            }
        }

        binding.noEndDateCb.setOnCheckedChangeListener { _, isChecked ->
            run {
                hideKeyboard()
                binding.endDateBtn.isEnabled = !isChecked
                currentlyEditedLoan.paymentDateInMs = 0
            }
        }

        addInterestFragment()
        binding.enableInterestCb.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (initialYPositionOfInterestFragment == 0.0f) {
                initialYPositionOfInterestFragment = binding.newLoanFragmentContainer.y
                if (initialYPositionOfInterestFragment == 0.0f && isChecked) {
                    binding.newLoanFragmentContainer.visibility = View.VISIBLE
                    return@setOnCheckedChangeListener
                }
            }
            val offset = 160.0f
            hideKeyboard()
            if (isChecked) {
                binding.newLoanFragmentContainer.apply {
                    y = initialYPositionOfInterestFragment + offset
                    alpha = 0.0f
                    visibility = View.VISIBLE
                }.also {
                    it.animate()
                            .yBy(-offset)
                            .alpha(1.0f)
                            .setDuration(300L)
                            .start()
                }
            } else {
                binding.newLoanFragmentContainer.animate()
                        .yBy(offset)
                        .setDuration(300L)
                        .alpha(0.0f)
                        .withEndAction { binding.newLoanFragmentContainer.visibility = View.INVISIBLE }
                        .start()
            }
        }
        if (binding.enableInterestCb.isChecked) {
            binding.newLoanFragmentContainer.visibility = View.VISIBLE
        } else {
            binding.newLoanFragmentContainer.visibility = View.INVISIBLE
        }
    }

    private fun addInterestFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.new_loan_fragment_container, interestFragment)
        fragmentTransaction.commit()
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

        if (binding.enableInterestCb.isChecked) {
            if (interestRateHasChanged()) {
                currentlyEditedLoan.interestRate = convertInterestRateToDouble(wholePercentPart, decimalPercentPart)
            }
            if (periodInDaysHasChanged()) {
                currentlyEditedLoan.periodInDays = periodInDays
                currentlyEditedLoan.nextChargingDateInMs =
                        calculateNextChargingTime(NewLoanVmHelper().normalizeTime(Calendar.getInstance().timeInMillis),
                                periodInDays)
            }
        } else {
            currentlyEditedLoan.interestRate = 0.0
            currentlyEditedLoan.periodInDays = 0
            currentlyEditedLoan.nextChargingDateInMs = 0
        }
    }

    private fun interestRateHasChanged(): Boolean {
        return (viewModel.initialWholePercentPart != wholePercentPart || viewModel.initialDecimalPercentPart != decimalPercentPart)
    }

    private fun periodInDaysHasChanged(): Boolean {
        return (viewModel.initialPeriodInDays != periodInDays)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Activity.RESULT_FIRST_USER && resultCode == Activity.RESULT_OK) {
            val contactUri: Uri?
            if (data != null) {
                contactUri = data.data
                val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                var cursor: Cursor? = null
                if (contactUri != null) {
                    cursor = contentResolver.query(contactUri,
                            projection,
                            null,
                            null,
                            null)
                }
                cursor?.let { parseContactInfo(it) }
                cursor?.close()
            }
        }
    }

    private fun parseContactInfo(cursor: Cursor) {
        if (cursor.moveToFirst()) {
            val numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val number = cursor.getString(numberColumnIndex)
            val name = cursor.getString(nameColumnIndex)
            binding.editTextName.setText(name)
            binding.editTextPhone.setText(number)
            currentlyEditedLoan.debtorName = name
            currentlyEditedLoan.phoneNumber = number
        }
    }

    fun showInterestRateError() {
       if (!zeroRateErrorDialog.isShowing) {
           zeroRateErrorDialog.show()
       }
    }

    private fun onCancelLoanPressed() {
        showConfirmCancelDialog()
    }

    private fun buildDialogs() {
        val cancelDialogBuilder = AlertDialog.Builder(this)
        cancelDialogBuilder.setMessage(R.string.confirm_cancel_dialog_msg)
                .setPositiveButton(R.string.confirm_cancel_dialog_positive) { _, _ -> onBackPressed() }
                .setNegativeButton(R.string.confirm_cancel_dialog_negative) { _, _ -> }
        confirmCancelDialog = cancelDialogBuilder.create()

        val zeroRateErrorBuilder = AlertDialog.Builder(this)
        zeroRateErrorBuilder.setMessage(R.string.interest_rate_zero_msg)
                .setPositiveButton("OK") { _: DialogInterface?, _: Int -> }
        zeroRateErrorDialog = zeroRateErrorBuilder.create()
    }

    private fun showConfirmCancelDialog() {
        if (!confirmCancelDialog.isShowing) {
            confirmCancelDialog.show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        with(Calendar.getInstance()) {
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
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
        finish()
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
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
