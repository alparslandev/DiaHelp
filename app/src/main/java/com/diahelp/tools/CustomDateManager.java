package com.diahelp.tools;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Alparslan on 5.11.2016.
 */

public class CustomDateManager {
    private static Calendar c;
    private static SimpleDateFormat formatter;
    private static SimpleDateFormat wellFormed;

    private static void initialize() {
        c = Calendar.getInstance(Locale.getDefault());
        formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z", Locale.getDefault());
        wellFormed = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    }

    public static String getDateShort(String date, Context context) {
        initialize();
        c.setTime(toDate(date));
        return android.text.format.DateUtils.formatDateTime(context,
                c.getTimeInMillis(), android.text.format.DateUtils.FORMAT_SHOW_DATE);
    }

    public static String getTodayWellFormedString() {
        initialize();
        return wellFormed.format(c.getTime());
    }

    public static Date toDate(String text) {
        initialize();
        try {
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
            Date date = df.parse(text);
            return CustomDateManager.getTomorrow(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date getTimeOfDay() {
        initialize();
        return c.getTime();
    }

    public static String getDateOfDay() {
        initialize();
        String dateOfDay = c.get(Calendar.DAY_OF_WEEK) + "." + c.get(Calendar.MONTH) + "." +
                c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) +
                ":" + c.get(Calendar.SECOND) + c.getTimeZone();
        return dateOfDay;
    }

    public static Date getToday() {
        initialize();
        return c.getTime();
    }

    public static Date getTimeOneWeekBefore() {
        initialize();
        try {
            c.add(Calendar.DAY_OF_MONTH, -7);
            String date = formatter.format(c.getTime());
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDateBefore(Date sendedDate, int position) {
        initialize();

        c.setTime(sendedDate);
        c.add(Calendar.DAY_OF_MONTH, ((-1) * position));
        return wellFormed.format(c.getTime());
    }

    public static Date getYesterday(Date sendedDate) {
        initialize();
        c.setTime(sendedDate);
        c.add(Calendar.DAY_OF_YEAR, (-1));
        return c.getTime();
    }

    public static Date getTomorrow(Date sendedDate) {
        initialize();
        c.setTime(sendedDate);
        c.add(Calendar.DAY_OF_YEAR, (1));
        return c.getTime();
    }
}
