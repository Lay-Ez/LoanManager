package com.romanoindustries.loanmanager.roomdb;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.romanoindustries.loanmanager.datamodel.InterestAccrualEvent;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;

public class RoomConvertersTest {


    @Test
    public void testCalendarWithGson() {

        List<InterestAccrualEvent> chargeEvents = new ArrayList<>();
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        Calendar calendar3 = Calendar.getInstance();
        Calendar calendar4 = Calendar.getInstance();

        InterestAccrualEvent event1 = new InterestAccrualEvent(calendar1, 100, 200);
        InterestAccrualEvent event2 = new InterestAccrualEvent(calendar2, 33, 245);
        InterestAccrualEvent event3 = new InterestAccrualEvent(calendar3, 234234, 234234);
        InterestAccrualEvent event4 = new InterestAccrualEvent(calendar4, 1444, 24234);

        chargeEvents.add(event1);
        chargeEvents.add(event2);
        chargeEvents.add(event3);
        chargeEvents.add(event4);

        Gson gson = new Gson();

        String json1 = gson.toJson(chargeEvents);
        ArrayList<InterestAccrualEvent> chargeEventsResult = gson.fromJson(json1, new TypeToken<List<InterestAccrualEvent>>(){}.getType());
        chargeEventsResult.forEach(new Consumer<InterestAccrualEvent>() {
            @Override
            public void accept(InterestAccrualEvent interestAccrualEvent) {
                System.out.println(interestAccrualEvent.getChargeEventDate().getTimeInMillis());
                System.out.println(interestAccrualEvent.getStartAmount());
                System.out.println(interestAccrualEvent.getEndAmount());
            }
        });
    }
}