package com.romanoindustries.loanmanager.editloan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.romanoindustries.loanmanager.R
import com.romanoindustries.loanmanager.databinding.EditLoanInterestFragmentBinding

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
        val interestSpinner = view.findViewById<Spinner>(R.id.interest_period_spinner)
        val spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.periods,
                android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        interestSpinner.adapter = spinnerAdapter
        interestSpinner.onItemSelectedListener = this
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }
}




















