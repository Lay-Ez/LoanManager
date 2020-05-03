package com.romanoindustries.loanmanager.editloan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.romanoindustries.loanmanager.R
import com.romanoindustries.loanmanager.databinding.EditLoanInterestFragmentBinding
import java.util.*

const val LOAN_PERIOD_ONE_DAY = 1
const val LOAN_PERIOD_THREE_DAYS = 3
const val LOAN_PERIOD_ONE_WEEK = 7
const val LOAN_PERIOD_TWO_WEEKS = 14
const val LOAN_PERIOD_ONE_MONTH = 30
const val LOAN_PERIOD_ONE_YEAR = 365

class EditLoanInterestFragment : Fragment(), OnItemSelectedListener {

    private lateinit var binding: EditLoanInterestFragmentBinding
    private lateinit var viewModel: EditLoanViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.edit_loan_interest_fragment, container, false)
        binding = EditLoanInterestFragmentBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity()).get(EditLoanViewModel::class.java)
        setUpViews(view)
        return view
    }

    private fun setUpViews(view: View) {
        val spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.periods,
                android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        val periodSpinner = view.findViewById<Spinner>(R.id.interest_period_spinner)
        periodSpinner.adapter = spinnerAdapter
        periodSpinner.onItemSelectedListener = this
        viewModel.periodInDays.observe(this, androidx.lifecycle.Observer {
            when(it) {
                LOAN_PERIOD_ONE_DAY -> periodSpinner.setSelection(0)
                LOAN_PERIOD_THREE_DAYS -> periodSpinner.setSelection(1)
                LOAN_PERIOD_ONE_WEEK -> periodSpinner.setSelection(2)
                LOAN_PERIOD_TWO_WEEKS -> periodSpinner.setSelection(3)
                LOAN_PERIOD_ONE_MONTH -> periodSpinner.setSelection(4)
                LOAN_PERIOD_ONE_YEAR -> periodSpinner.setSelection(5)
                else -> periodSpinner.setSelection(0)
            }
        })

        val wholePercentNp = view.findViewById<NumberPicker>(R.id.whole_np).apply {
            minValue = 0
            maxValue = 99
            setOnValueChangedListener { _, _, newVal -> viewModel.setWholePercent(newVal) }
        }
        viewModel.wholePercent.observe(this, androidx.lifecycle.Observer { wholePercentNp.value = it })

        val decimalPercentNp = view.findViewById<NumberPicker>(R.id.decimal_np).apply {
            minValue = 0
            maxValue = 99
            setFormatter { String.format(Locale.US, "%02d", it) }
            setOnValueChangedListener { _, _, newVal -> viewModel.setDecimalPercent(newVal) }
        }
        viewModel.decimalPercent.observe(this, androidx.lifecycle.Observer { decimalPercentNp.value = it })
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(position) {
            0 -> viewModel.setPeriodInDays(LOAN_PERIOD_ONE_DAY)
            1 -> viewModel.setPeriodInDays(LOAN_PERIOD_THREE_DAYS)
            2 -> viewModel.setPeriodInDays(LOAN_PERIOD_ONE_WEEK)
            3 -> viewModel.setPeriodInDays(LOAN_PERIOD_TWO_WEEKS)
            4 -> viewModel.setPeriodInDays(LOAN_PERIOD_ONE_MONTH)
            5 -> viewModel.setPeriodInDays(LOAN_PERIOD_ONE_YEAR)
            else -> viewModel.setPeriodInDays(1)
        }
    }
}




















