package com.rki.essenAufRaedern.util;

import java.util.Calendar;
import java.util.Date;

public class Util {
    public static class DateUtil extends java.util.Date {
        public static String getDayOfWeekName(int dayOfWeek) {
            return switch (dayOfWeek) {
                case Calendar.MONDAY -> "Montag";
                case Calendar.TUESDAY -> "Dienstag";
                case Calendar.WEDNESDAY -> "Mittwoch";
                case Calendar.THURSDAY -> "Donnerstag";
                case Calendar.FRIDAY -> "Freitag";
                case Calendar.SATURDAY -> "Samstag";
                case Calendar.SUNDAY -> "Sonntag";
                default -> throw new IllegalArgumentException("Invalid day of week: " + dayOfWeek + "! Range: 0-6");
            };
        }

        public static Date getDayFromNow(int offset) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, offset);

            return cal.getTime();
        }

        public static boolean isToday(Date date) {
            Calendar c1 = Calendar.getInstance();
            c1.setTime(new Date());

            Calendar c2 = Calendar.getInstance();
            c2.setTime(date);

            return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                    && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                    && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH));
        }
    }
}
