package com.romanoindustries.loanmanager.currency;

import android.content.Context;
import android.view.MenuItem;

import androidx.appcompat.widget.PopupMenu;

import com.romanoindustries.loanmanager.R;

public class CurrencyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

    private Context context;

    public CurrencyMenuItemClickListener(Context context) {
        this.context = context;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.isChecked()) {
            return true;
        }
        item.setChecked(true);

        switch (item.getItemId()) {

            case R.id.usd_mnu_item:
                CurrencyHelper.setCurrency(context, CurrencyHelper.USD);
                break;

            case R.id.rub_mnu_item:
                CurrencyHelper.setCurrency(context, CurrencyHelper.RUB);
                break;

            case R.id.eur_mnu_item:
                CurrencyHelper.setCurrency(context, CurrencyHelper.EUR);
                break;

            case R.id.cad_mnu_item:
                CurrencyHelper.setCurrency(context, CurrencyHelper.CAD);
                break;

            case R.id.aud_mnu_item:
                CurrencyHelper.setCurrency(context, CurrencyHelper.AUD);
                break;

            case R.id.inr_mnu_item:
                CurrencyHelper.setCurrency(context, CurrencyHelper.INR);
                break;

        }
        return true;
    }
}

















