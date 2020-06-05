package com.romanoindustries.loanmanager.roomdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.romanoindustries.loanmanager.datamodel.Loan;

import java.util.List;

@Dao
public interface LoanDao {

    @Insert
    void insert(Loan loan);

    @Update
    void update(Loan loan);

    @Delete
    void delete(Loan loan);

    @Query("DELETE FROM loan_table")
    void deleteAllLoans();

    @Query("UPDATE loan_table SET highlighted=:highlighted WHERE id=:loanId")
    void setHighlighted(int loanId, boolean highlighted);

    @Query("SELECT * FROM loan_table")
    LiveData<List<Loan>> getAllLoans();

    @Query("SELECT * FROM loan_table WHERE type=" + Loan.TYPE_IN + " OR type=" + Loan.TYPE_OUT)
    List<Loan> getAllActiveLoans();

    @Query("SELECT * FROM loan_table WHERE type=" + Loan.TYPE_IN)
    LiveData<List<Loan>> getInLoans();

    @Query("SELECT * FROM loan_table WHERE type=" + Loan.TYPE_OUT)
    LiveData<List<Loan>> getOutLoans();

    @Query("SELECT * FROM loan_table WHERE type=" + Loan.TYPE_ARCHIVED_IN + " OR type=" + Loan.TYPE_ARCHIVED_OUT)
    LiveData<List<Loan>> getArchivedLoans();

    @Query("SELECT * FROM loan_table WHERE id=:id")
    Loan findLoanById(int id);
}






















