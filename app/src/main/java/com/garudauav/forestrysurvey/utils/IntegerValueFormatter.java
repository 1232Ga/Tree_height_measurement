package com.garudauav.forestrysurvey.utils;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class IntegerValueFormatter extends ValueFormatter {


    @Override
    public String getFormattedValue(float value) {
        // Round the float value to an integer and return it as a string
        return String.valueOf(Math.round(value));
    }
}
