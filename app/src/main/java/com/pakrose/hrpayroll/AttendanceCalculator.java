
package com.pakrose.hrpayroll;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AttendanceCalculator {

    private static int toMinutes(String time) {
        try {
            SimpleDateFormat format =
                    new SimpleDateFormat("HH:mm", Locale.US);
            format.setLenient(false);

            Date date = format.parse(time);

            if (date == null) return 0;

            java.util.Calendar cal =
                    java.util.Calendar.getInstance();
            cal.setTime(date);

            return cal.get(java.util.Calendar.HOUR_OF_DAY) * 60
                    + cal.get(java.util.Calendar.MINUTE);

        } catch (Exception e) {
            return 0;
        }
    }

    public static int lateMinutes(String checkIn,
                                  String officeStart,
                                  int graceMinutes) {

        int actual = toMinutes(checkIn);
        int start = toMinutes(officeStart);

        return Math.max(0, actual - start - graceMinutes);
    }

    public static int earlyMinutes(String checkOut,
                                   String officeEnd) {

        int actual = toMinutes(checkOut);
        int end = toMinutes(officeEnd);

        return Math.max(0, end - actual);
    }

    public static int overtimeMinutes(String checkOut,
                                      String officeEnd) {

        int actual = toMinutes(checkOut);
        int end = toMinutes(officeEnd);

        return Math.max(0, actual - end);
    }

    public static int workedMinutes(String checkIn,
                                    String checkOut,
                                    int breakMinutes) {

        int start = toMinutes(checkIn);
        int end = toMinutes(checkOut);

        if (end < start) {
            end += 24 * 60;
        }

        return Math.max(0, end - start - breakMinutes);
    }

    public static int totalMinutes(String hours) {
        try {
            return Integer.parseInt(hours);
        } catch (Exception e) {
            return 0;
        }
    }
}
