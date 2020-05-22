package com.sungfamily;

import android.content.Context;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Utils {

    public static void toast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static String getFormattedDateTime(long time)
    {
        DateTime dt = new DateTime(time, DateTimeZone.UTC);
        dt = dt.withZone(DateTimeZone.getDefault());
        DateTimeFormatter f = DateTimeFormat.forPattern("MMM dd, hh:mm a");
        return dt.toString(f);
    }

}
