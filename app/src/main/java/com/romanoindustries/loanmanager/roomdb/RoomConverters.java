package com.romanoindustries.loanmanager.roomdb;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.romanoindustries.loanmanager.datamodel.InterestChargeEvent;

import java.util.Calendar;
import java.util.List;

public class RoomConverters {

    @TypeConverter
    public static long calendarToMillis(Calendar calendar) {
        return calendar.getTimeInMillis();
    }

    @TypeConverter
    public static Calendar millisToCalendar(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }

    @TypeConverter
    public static String listToString(List<InterestChargeEvent> chargeEvents) {
        Gson gson = new Gson();
        return gson.toJson(chargeEvents);
    }

    @TypeConverter
    public static List<InterestChargeEvent> stringToList(String storedJsonString) {
        Gson gson = new Gson();
        return gson.fromJson(storedJsonString, new TypeToken<List<InterestChargeEvent>>(){}.getType());
    }

}
