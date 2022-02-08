package com.babydocs.utils;

import java.util.Calendar;
import java.util.Date;

public class AppUtils
{
    public static Date addHours(Date date, int hours)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

}
