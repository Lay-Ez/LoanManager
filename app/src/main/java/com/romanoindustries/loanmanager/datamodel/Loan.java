package com.romanoindustries.loanmanager.datamodel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@Entity (tableName = "loan_table")
public class Loan {

    @Ignore
    public static final int TYPE_IN = 1;
    @Ignore
    public static final int TYPE_OUT = 2;
    @Ignore
    public static final int TYPE_ARCHIVED_IN = 3;
    @Ignore
    public static final int TYPE_ARCHIVED_OUT = 4;

    public Loan() {}

    private int type;

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
    private String phoneNumber;

    @ColumnInfo (name = "start_date")
    private long startDateInMs;

    @ColumnInfo (name = "payment_date")
    private long paymentDateInMs;

    @ColumnInfo (name = "next_charging_date")
    private long nextChargingDateInMs;

    @ColumnInfo (name = "archived_date", defaultValue = "1")
    private long archivedDateInMs;

    @ColumnInfo (name = "period_in_days")
    private int periodInDays;

    @ColumnInfo (name = "interest_rate")
    private double interestRate;

    @ColumnInfo (defaultValue = "false")
    private boolean highlighted;

    @ColumnInfo (name = "charge_events")
    private List<InterestAccrualEvent> chargeEvents;

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getStartDateInMs() {
        return startDateInMs;
    }

    public void setStartDateInMs(long startDateInMs) {
        this.startDateInMs = startDateInMs;
    }

    public long getPaymentDateInMs() {
        return paymentDateInMs;
    }

    public void setPaymentDateInMs(long paymentDateInMs) {
        this.paymentDateInMs = paymentDateInMs;
    }

    public long getNextChargingDateInMs() {
        return nextChargingDateInMs;
    }

    public void setNextChargingDateInMs(long nextChargingDateInMs) {
        this.nextChargingDateInMs = nextChargingDateInMs;
    }

    public long getArchivedDateInMs() {
        return archivedDateInMs;
    }

    public void setArchivedDateInMs(long archivedDateInMs) {
        this.archivedDateInMs = archivedDateInMs;
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

    public List<InterestAccrualEvent> getChargeEvents() {
        return chargeEvents;
    }

    public void setChargeEvents(List<InterestAccrualEvent> chargeEvents) {
        this.chargeEvents = chargeEvents;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    @NotNull
    @Override
    public String toString() {
        return "Loan{" +
                "type=" + type +
                ", id=" + id +
                ", debtorName='" + debtorName + '\'' +
                ", specialNote='" + specialNote + '\'' +
                ", startAmount=" + startAmount +
                ", currentAmount=" + currentAmount +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", startDateInMs=" + startDateInMs +
                ", paymentDateInMs=" + paymentDateInMs +
                ", nextChargingDateInMs=" + nextChargingDateInMs +
                ", periodInDays=" + periodInDays +
                ", interestRate=" + interestRate +
                '}';
    }

}
