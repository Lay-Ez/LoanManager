package com.romanoindustries.loanmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.util.Attributes;
import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.datamodel.Loan;

import java.util.List;

public class LoansAdapter extends RecyclerSwipeAdapter<LoansAdapter.LoanViewHolder> {

    private List<Loan>loans;

    public LoansAdapter(List<Loan> loans) {
        this.loans = loans;
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

        return new LoanViewHolder(view);
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

        private SwipeLayout swipeLayout;
        private TextView nameTv;
        private ImageButton btn;

        public LoanViewHolder(@NonNull View itemView) {
            super(itemView);

            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            nameTv = itemView.findViewById(R.id.loan_name_text_view);
            btn = itemView.findViewById(R.id.imageButton);
        }

        public void bind(Loan loan) {
            nameTv.setText(loan.getDebtorName());
        }
    }
}
