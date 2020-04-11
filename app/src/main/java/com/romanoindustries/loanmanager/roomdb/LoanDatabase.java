package com.romanoindustries.loanmanager.roomdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.romanoindustries.loanmanager.datamodel.Loan;

@Database(entities = Loan.class, version = 2)
@TypeConverters(RoomConverters.class)
public abstract class LoanDatabase extends RoomDatabase {

    private static LoanDatabase instance;

    public abstract LoanDao loanDao();

    public static synchronized LoanDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    LoanDatabase.class,
                    "loan_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }

}
