package com.romanoindustries.loanmanager.roomdb;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.romanoindustries.loanmanager.datamodel.InterestChargeEvent;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;

public class RoomConvertersTest {


    @Test
    public void testCalendarWithGson() {

        List<InterestChargeEvent> chargeEvents = new ArrayList<>();
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        Calendar calendar3 = Calendar.getInstance();
        Calendar calendar4 = Calendar.getInstance();

        InterestChargeEvent event1 = new InterestChargeEvent(calendar1, 100, 200);
        InterestChargeEvent event2 = new InterestChargeEvent(calendar2, 33, 245);
        InterestChargeEvent event3 = new InterestChargeEvent(calendar3, 234234, 234234);
        InterestChargeEvent event4 = new InterestChargeEvent(calendar4, 1444, 24234);

        chargeEvents.add(event1);
        chargeEvents.add(event2);
        chargeEvents.add(event3);
        chargeEvents.add(event4);

        Gson gson = new Gson();

        String json1 = gson.toJson(chargeEvents);
        ArrayList<InterestChargeEvent> chargeEventsResult = gson.fromJson(json1, new TypeToken<List<InterestChargeEvent>>(){}.getType());
        chargeEventsResult.forEach(new Consumer<InterestChargeEvent>() {
            @Override
            public void accept(InterestChargeEvent interestChargeEvent) {
                System.out.println(interestChargeEvent.getChargeEventDate().getTimeInMillis());
                System.out.println(interestChargeEvent.getStartAmount());
                System.out.println(interestChargeEvent.getEndAmount());
            }
        });
    }
}