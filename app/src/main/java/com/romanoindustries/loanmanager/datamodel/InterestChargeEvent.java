package com.romanoindustries.loanmanager.datamodel;

import java.util.Calendar;

public class InterestChargeEvent {
    private Calendar chargeEventDate;
    private int startAmount;
    private int endAmount;

    public InterestChargeEvent(Calendar chargeEventDate, int startAmount, int endAmount) {
        this.chargeEventDate = chargeEventDate;
        this.startAmount = startAmount;
        this.endAmount = endAmount;
    }

    public Calendar getChargeEventDate() {
        return chargeEventDate;
    }

    public void setChargeEventDate(Calendar chargeEventDate) {
        this.chargeEventDate = chargeEventDate;
    }

    public int getStartAmount() {
        return startAmount;
    }

    public void setStartAmount(int startAmount) {
        this.startAmount = startAmount;
    }

    public int getEndAmount() {
        return endAmount;
    }

    public void setEndAmount(int endAmount) {
        this.endAmount = endAmount;
    }
}
