package com.romanoindustries.loanmanager.loanrepo;

import android.os.AsyncTask;
import android.util.Pair;

import com.romanoindustries.loanmanager.roomdb.LoanDao;

public class LoanHighlightAsync extends AsyncTask<Pair<Integer, Boolean>, Void, Void> {

    private LoanDao loanDao;

    public LoanHighlightAsync(LoanDao loanDao) {
        this.loanDao = loanDao;
    }

    @Override
    protected Void doInBackground(Pair<Integer, Boolean>... pairs) {
        int loanId = pairs[0].first;
        boolean highlight = pairs[0].second;
        loanDao.setHighlighted(loanId, highlight);
        return null;
    }
}
