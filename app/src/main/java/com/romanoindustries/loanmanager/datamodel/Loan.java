package com.romanoindustries.loanmanager.datamodel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.List;

@Entity (tableName = "loan_table")
public class Loan {

    @Ignore
    public static final int TYPE_IN = 1;
    @Ignore
    public static final int TYPE_OUT = 2;
    @Ignore
    public static final int TYPE_ARCHIVED = 3;

    public Loan() {}

    @PrimaryKey (autoGenerate = true)
    private int id;

    @ColumnInfo (name = "debtor_name")
    private String debtorName;

    @ColumnInfo ( name = "special_note")
    private String specialNote;

    @ColumnInfo (name = "start_amount")
    private int startAmount;

    @ColumnInfo (name = "current_amount")
    private int currentAmount;

    @ColumnInfo (name = "phone_number")
    private int phoneNumber;

    @ColumnInfo (name = "start_date")
    private Calendar startDate;

    @ColumnInfo (name = "payment_date")
    private Calendar paymentDate;

    @ColumnInfo (name = "next_charging_date")
    private Calendar nextChargingDate;

    @ColumnInfo (name = "period_in_days")
    private int periodInDays;

    @ColumnInfo (name = "interest_rate")
    private double interestRate;

    private int type;

    @ColumnInfo (name = "charge_events")
    private List<InterestChargeEvent> chargeEvents;

    public String getDebtorName() {
        return debtorName;
    }

    public void setDebtorName(String debtorName) {
        this.debtorName = debtorName;
    }

    public String getSpecialNote() {
        return specialNote;
    }

    public void setSpecialNote(String specialNote) {
        this.specialNote = specialNote;
    }

    public int getStartAmount() {
        return startAmount;
    }

    public void setStartAmount(int startAmount) {
        this.startAmount = startAmount;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Calendar paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Calendar getNextChargingDate() {
        return nextChargingDate;
    }

    public void setNextChargingDate(Calendar nextChargingDate) {
        this.nextChargingDate = nextChargingDate;
    }

    public int getPeriodInDays() {
        return periodInDays;
    }

    public void setPeriodInDays(int periodInDays) {
        this.periodInDays = periodInDays;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<InterestChargeEvent> getChargeEvents() {
        return chargeEvents;
    }

    public void setChargeEvents(List<InterestChargeEvent> chargeEvents) {
        this.chargeEvents = chargeEvents;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
