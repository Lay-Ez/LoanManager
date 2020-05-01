package com.romanoindustries.loanmanager.editloan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.romanoindustries.loanmanager.R
import com.romanoindustries.loanmanager.databinding.ActivityEditLoanBinding
import com.romanoindustries.loanmanager.datamodel.Loan
import com.romanoindustries.loanmanager.viewmodels.LoansViewModel
import kotlinx.android.synthetic.main.activity_new_loan.*

const val LOAN_ID_KEY = "loan_id_key"
const val TAG = "EditLoanActivity"

class EditLoanActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditLoanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditLoanBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
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
        binding.editTextName.setText(loan.debtorName)
        binding.editTextPhone.setText(loan.phoneNumber)
        binding.editTextAmount.setText(loan.currentAmount.toString())
        binding.editTextNote.setText(loan.specialNote)
    }
}
