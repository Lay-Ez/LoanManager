package com.romanoindustries.loanmanager.archivedloans;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.util.Attributes;
import com.romanoindustries.loanmanager.MainActivity;
import com.romanoindustries.loanmanager.MyApp;
import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.datamodel.Loan;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.romanoindustries.loanmanager.newloan.NewLoanInterestFragment.LOAN_PERIOD_ONE_DAY;
import static com.romanoindustries.loanmanager.newloan.NewLoanInterestFragment.LOAN_PERIOD_ONE_MONTH;
import static com.romanoindustries.loanmanager.newloan.NewLoanInterestFragment.LOAN_PERIOD_ONE_WEEK;
import static com.romanoindustries.loanmanager.newloan.NewLoanInterestFragment.LOAN_PERIOD_ONE_YEAR;
import static com.romanoindustries.loanmanager.newloan.NewLoanInterestFragment.LOAN_PERIOD_THREE_DAYS;
import static com.romanoindustries.loanmanager.newloan.NewLoanInterestFragment.LOAN_PERIOD_TWO_WEEKS;

public class ArchivedLoansAdapter extends RecyclerSwipeAdapter<ArchivedLoansAdapter.ArchLoanViewHolder> {

    private List<Loan> loans;
    private ArchOnLoanListener onLoanListener;
    private String currencyLabel;

    ArchivedLoansAdapter(List<Loan> loans, ArchOnLoanListener onLoanListener) {
        this.loans = loans;
        this.onLoanListener = onLoanListener;
        mItemManger.setMode(Attributes.Mode.Single);
    }

    public void setCurrencyLabel(String currencyLabel) {
        this.currencyLabel = currencyLabel;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    void updateLoans(List<Loan> loans) {
        DiffUtilCallbackLoansArch callbackLoans = new DiffUtilCallbackLoansArch(getLoans(), loans);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callbackLoans);
        this.loans = new ArrayList<>(loans);
        result.dispatchUpdatesTo(this);
    }

    @NotNull
    @Override
    public ArchLoanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int listItemId = R.layout.loan_list_item_archived;

        View view = inflater.inflate(listItemId, parent, false);
        return new ArchLoanViewHolder(view, onLoanListener);
    }

    @Override
    public void onBindViewHolder(ArchLoanViewHolder archLoanViewHolder, int i) {
        Loan loanToBind = loans.get(i);
        archLoanViewHolder.bind(loanToBind);
        mItemManger.bindView(archLoanViewHolder.itemView, i);
    }

    @Override
    public int getItemCount() {
        return loans.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.arch_swipe_layout;
    }

    class ArchLoanViewHolder extends RecyclerView.ViewHolder {

        private SwipeLayout swipeLayout;
        private TextView nameTv;
        private TextView currentAmountTv;
        private TextView currencyTv;
        private TextView startDateTv;
        private TextView endDateTv;
        private TextView percentTv;
        private TextView periodTv;
        private ImageView outIv;
        private ImageView inIv;

        ArchLoanViewHolder(@NonNull View itemView, ArchOnLoanListener onLoanListener) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.arch_swipe_layout);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            ConstraintLayout mainLayout = itemView.findViewById(R.id.arch_main_layout);
            nameTv = itemView.findViewById(R.id.name_tv);
            currencyTv = itemView.findViewById(R.id.list_item_currency_label);
            currentAmountTv = itemView.findViewById(R.id.current_amount_tv);
            startDateTv = itemView.findViewById(R.id.start_date_tv);
            endDateTv = itemView.findViewById(R.id.end_date_tv);
            percentTv = itemView.findViewById(R.id.percent_tv);
            periodTv = itemView.findViewById(R.id.period_tv);
            outIv = itemView.findViewById(R.id.arch_list_item_out_im);
            inIv = itemView.findViewById(R.id.arch_list_item_in_iv);
            ImageButton btnDelete = itemView.findViewById(R.id.delete_forever_ib);
            ImageButton btnUnarchive = itemView.findViewById(R.id.unarchive_ib);

            mainLayout.setOnClickListener(v -> {
                onLoanListener.onLoanCLicked(getAdapterPosition());
                swipeLayout.close();
            });

            btnUnarchive.setOnClickListener(v -> {
                onLoanListener.onLoanUnarchiveClicked(getAdapterPosition());
                swipeLayout.close();
            });

            btnDelete.setOnClickListener(v -> {
                onLoanListener.onLoanDeleteClicked(getAdapterPosition());
                swipeLayout.close();
            });
        }

        void bind(Loan loan) {

            Context context = MyApp.getContext();

            nameTv.setText(loan.getDebtorName());
            currentAmountTv.setText(MainActivity.formatAmount(loan.getCurrentAmount()));
            currencyTv.setText(currencyLabel);

            if (loan.getType() == Loan.TYPE_ARCHIVED_IN) {
                inIv.setVisibility(View.VISIBLE);
                outIv.setVisibility(View.GONE);
            } else if (loan.getType() == Loan.TYPE_ARCHIVED_OUT) {
                inIv.setVisibility(View.GONE);
                outIv.setVisibility(View.VISIBLE);
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(loan.getPaymentDateInMs());
            String endDateString = DateFormat.getDateInstance().format(calendar.getTime());
            endDateTv.setText(endDateString);

            calendar.setTimeInMillis(loan.getStartDateInMs());
            String startDateString = DateFormat.getDateInstance().format(calendar.getTime());
            startDateTv.setText(startDateString);

            String periodStr = context.getString(R.string.list_item_empty_placeholder);
            if (loan.getInterestRate() != 0) {
                String percentRateStr = loan.getInterestRate() + "%";
                percentTv.setText(percentRateStr);


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
            } else {
                percentTv.setText(periodStr);
                periodTv.setText(periodStr);
            }
        }
    }

    public class DiffUtilCallbackLoansArch extends DiffUtil.Callback {

        private List<Loan> oldList;
        private List<Loan> newList;

        public DiffUtilCallbackLoansArch(List<Loan> oldList, List<Loan> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Loan loan1 = oldList.get(oldItemPosition);
            Loan loan2 = newList.get(newItemPosition);
            return loan1.getId() == loan2.getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Loan loan1 = oldList.get(oldItemPosition);
            Loan loan2 = newList.get(newItemPosition);

            return (loan1.getDebtorName().equals(loan2.getDebtorName())) &&
                    loan1.getCurrentAmount() == loan2.getCurrentAmount() &&
                    loan1.getPaymentDateInMs() == loan2.getPaymentDateInMs() &&
                    loan1.getStartDateInMs() == loan2.getStartDateInMs() &&
                    loan1.getType() == loan2.getType() &&
                    loan1.getInterestRate() == loan2.getInterestRate() &&
                    loan1.getPeriodInDays() == loan2.getPeriodInDays();
        }
    }

    public interface ArchOnLoanListener {
        void onLoanCLicked(int position);
        void onLoanDeleteClicked(int position);
        void onLoanUnarchiveClicked(int position);
    }
}
