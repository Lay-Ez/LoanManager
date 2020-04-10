package com.romanoindustries.loanmanager.datamodel;

import java.math.BigDecimal;
import java.util.Calendar;

public class InterestChargeEvent {
    private Calendar chargeEventDate;
    private BigDecimal startAmount;
    private BigDecimal endAmount;

    public InterestChargeEvent(Calendar chargeEventDate, BigDecimal startAmount, BigDecimal endAmount) {
        this.chargeEventDate = chargeEventDate;
        this.startAmount = startAmount;
        this.endAmount = endAmount;
    }
}
