package com.garudauav.forestrysurvey.db.convertors;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Convertors {

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        try {
            return (date != null) ? FORMATTER.parse(FORMATTER.format(date)).getTime() : null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
