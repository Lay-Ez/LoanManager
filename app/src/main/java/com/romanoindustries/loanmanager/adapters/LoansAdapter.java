package com.romanoindustries.loanmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.util.Attributes;
import com.romanoindustries.loanmanager.MyApp;
import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.datamodel.Loan;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.romanoindustries.loanmanager.newloan.InterestFragment.LOAN_PERIOD_ONE_DAY;
import static com.romanoindustries.loanmanager.newloan.InterestFragment.LOAN_PERIOD_ONE_MONTH;
import static com.romanoindustries.loanmanager.newloan.InterestFragment.LOAN_PERIOD_ONE_WEEK;
import static com.romanoindustries.loanmanager.newloan.InterestFragment.LOAN_PERIOD_ONE_YEAR;
import static com.romanoindustries.loanmanager.newloan.InterestFragment.LOAN_PERIOD_THREE_DAYS;
import static com.romanoindustries.loanmanager.newloan.InterestFragment.LOAN_PERIOD_TWO_WEEKS;

public class LoansAdapter extends RecyclerSwipeAdapter<LoansAdapter.LoanViewHolder> {

    private List<Loan>loans;
    private OnLoanListener onLoanListener;

    public LoansAdapter(List<Loan> loans, OnLoanListener onLoanListener) {
        this.loans = loans;
        this.onLoanListener = onLoanListener;
        mItemManger.setMode(Attributes.Mode.Single);
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void updateLoans(List<Loan> loans) {
        this.loans = loans;
        notifyDataSetChanged();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_layout;
    }

    @NonNull
    @Override
    public LoanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int listItemResourceId = R.layout.loan_list_item;

        View view = inflater.inflate(listItemResourceId, parent, false);

        return new LoanViewHolder(view, onLoanListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LoanViewHolder holder, int position) {
        Loan loanToBind = loans.get(position);
        holder.bind(loanToBind);
        mItemManger.bindView(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return loans.size();
    }

    class LoanViewHolder extends RecyclerView.ViewHolder{

        private OnLoanListener onLoanListener;
        private ConstraintLayout mainLayout;

        private SwipeLayout swipeLayout;
        private TextView nameTv;
        private TextView currentAmountTv;
        private TextView endDateTv;
        private TextView percentTv;
        private TextView periodTv;
        private ImageButton btnDelete;
        private ImageButton btnEdit;

        public LoanViewHolder(@NonNull View itemView, OnLoanListener onLoanListener) {
            super(itemView);
            this.onLoanListener = onLoanListener;

            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            mainLayout = itemView.findViewById(R.id.list_item_main_body);
            nameTv = itemView.findViewById(R.id.name_tv);
            currentAmountTv = itemView.findViewById(R.id.current_amount_tv);
            endDateTv = itemView.findViewById(R.id.end_date_tv);
            percentTv = itemView.findViewById(R.id.percent_tv);
            periodTv = itemView.findViewById(R.id.period_tv);
            btnDelete = itemView.findViewById(R.id.delete_ib);
            btnEdit = itemView.findViewById(R.id.edit_ib);

            mainLayout.setOnClickListener(v -> {
                onLoanListener.onLoanCLicked(getAdapterPosition());
                swipeLayout.close();});
            btnDelete.setOnClickListener(v -> {
                onLoanListener.onLoanDeleteClicked(getAdapterPosition());
                swipeLayout.close();});
            btnEdit.setOnClickListener(v -> {
                onLoanListener.onLoadEditClicked(getAdapterPosition());
                swipeLayout.close();});
        }

        public void bind(Loan loan) {
            nameTv.setText(loan.getDebtorName());
            currentAmountTv.setText(NumberFormat.getNumberInstance(Locale.US).format(loan.getCurrentAmount()));

            if (loan.getPaymentDateInMs() != 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(loan.getPaymentDateInMs());
                String endDateString = DateFormat.getDateInstance().format(calendar.getTime());
                endDateTv.setText(endDateString);
            }

            if (loan.getInterestRate() != 0) {
                String percentRateStr = loan.getInterestRate() + "%";
                percentTv.setText(percentRateStr);

                Context context = MyApp.getContext();
                String periodStr = context.getString(R.string.list_item_empty_placeholder);

                switch (loan.getPeriodInDays()) {

                    case LOAN_PERIOD_ONE_DAY:
                        periodStr = context.getString(R.string.one_day);
                        break;

                    case LOAN_PERIOD_THREE_DAYS:
                        periodStr = context.getString(R.string.three_days);
                        break;

                    case LOAN_PERIOD_ONE_WEEK:
                        periodStr = context.getString(R.string.one_week);
                        break;

                    case LOAN_PERIOD_TWO_WEEKS:
                        periodStr = context.getString(R.string.two_weeks);
                        break;

                    case LOAN_PERIOD_ONE_MONTH:
                        periodStr = context.getString(R.string.one_month);
                        break;

                    case LOAN_PERIOD_ONE_YEAR:
                        periodStr = context.getString(R.string.one_year);
                        break;

                        default:
                            //
                }

                periodTv.setText(periodStr);
            }
        }
    }

    public interface OnLoanListener{
        void onLoanCLicked(int position);
        void onLoanDeleteClicked(int position);
        void onLoadEditClicked(int position);
    }
}
