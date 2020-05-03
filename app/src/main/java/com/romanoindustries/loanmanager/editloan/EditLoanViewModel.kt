package com.romanoindustries.loanmanager.editloan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.romanoindustries.loanmanager.MyApp
import com.romanoindustries.loanmanager.datamodel.Loan
import com.romanoindustries.loanmanager.loanrepo.LoanRepo


class EditLoanViewModel: ViewModel() {

    private val loanRepo = LoanRepo(MyApp.getApplication())
    var loanAlreadyFound = false
    val allLoans: LiveData<List<Loan>> = loanRepo.allLoans
    val editedLoan = MutableLiveData<Loan>()
    val wholePercent = MutableLiveData<Int>()
    val decimalPercent = MutableLiveData<Int>()
    val periodInDays = MutableLiveData<Int>()

    fun updateLoan(loan: Loan) = loanRepo.update(loan)

    fun setEditedLoan(loan: Loan?) {
        editedLoan.value = loan
    }

    fun setWholePercent(whole: Int) {
        wholePercent.value = whole
    }

    fun setDecimalPercent(decimal: Int) {
        decimalPercent.value = decimal
    }

    fun setPeriodInDays(days: Int) {
        periodInDays.value = days
    }
}

