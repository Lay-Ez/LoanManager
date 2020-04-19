package com.romanoindustries.loanmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.datamodel.Loan;

import java.util.List;

public class LoansAdapter extends RecyclerView.Adapter<LoansAdapter.LoanViewHolder> {

    private List<Loan>loans;
    private SwipeLayout lastOpenedLayout;

    public LoansAdapter(List<Loan> loans) {
        this.loans = loans;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void updateLoans(List<Loan> loans) {
        this.loans = loans;
        notifyDataSetChanged();
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
    }

    @Override
    public int getItemCount() {
        return loans.size();
    }

    class LoanViewHolder extends RecyclerView.ViewHolder{

        private SwipeLayout swipeLayout;
        private TextView nameTv;
        private Button btn;

        public LoanViewHolder(@NonNull View itemView) {
            super(itemView);

            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            swipeLayout.addSwipeListener(onlyOneOpenListener);
            nameTv = itemView.findViewById(R.id.loan_name_text_view);
            btn = itemView.findViewById(R.id.button);
        }

        public void bind(Loan loan) {
            nameTv.setText(loan.getDebtorName());
        }

        private SwipeLayout.SwipeListener onlyOneOpenListener = new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                if (lastOpenedLayout != null && lastOpenedLayout != layout) {
                    lastOpenedLayout.close();
                }
                lastOpenedLayout = layout;
            }

            @Override
            public void onOpen(SwipeLayout layout) {}

            @Override
            public void onStartClose(SwipeLayout layout) {}

            @Override
            public void onClose(SwipeLayout layout) {}

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {}

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {}
        };
    }
}
