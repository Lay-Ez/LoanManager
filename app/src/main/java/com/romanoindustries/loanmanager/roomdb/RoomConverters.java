package com.romanoindustries.loanmanager.roomdb;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.romanoindustries.loanmanager.datamodel.InterestAccrualEvent;

import java.util.List;

public class RoomConverters {

    @TypeConverter
    public static String listToString(List<InterestAccrualEvent> chargeEvents) {
        Gson gson = new Gson();
        return gson.toJson(chargeEvents);
    }

    @TypeConverter
    public static List<InterestAccrualEvent> stringToList(String storedJsonString) {
        Gson gson = new Gson();
        return gson.fromJson(storedJsonString, new TypeToken<List<InterestAccrualEvent>>(){}.getType());
    }

}
