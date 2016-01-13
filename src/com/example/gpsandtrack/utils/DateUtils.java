package com.example.gpsandtrack.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    /* 将字符串转为时间戳 */
    public static String getTimeToStamp(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒",
                Locale.CHINA);
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String tmptime = String.valueOf(date.getTime()).substring(0, 10);

        return tmptime;
    }
}
