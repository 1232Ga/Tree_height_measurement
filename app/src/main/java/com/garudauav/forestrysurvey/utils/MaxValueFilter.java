package com.garudauav.forestrysurvey.utils;

import android.text.InputFilter;
import android.text.Spanned;


public class MaxValueFilter implements InputFilter {
    private double maxValue;

    public MaxValueFilter(double maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            // Combine the existing text with the newly input text
            String beforeText = dest.toString().substring(0, dstart);
            String afterText = dest.toString().substring(dend, dest.length());
            String proposedText = beforeText + source.toString() + afterText;

            // Check if the proposed text exceeds the maximum value
            double input = Double.parseDouble(proposedText);
            if (input > maxValue) {
                return "";  // Block the input if it exceeds the max value
            }

            // Additional check for decimal places
            int dotIndex = proposedText.indexOf(".");
            if (dotIndex >= 0) {
                // Ensures that only two digits follow the decimal point
                String decimalPart = proposedText.substring(dotIndex + 1);
                if (decimalPart.length() > 2) {
                    return "";  // Block the input if more than two decimal places
                }
            }
        } catch (NumberFormatException nfe) {
            // Handle scenarios where the number format is incomplete (e.g., a lone "-", ".")
            // Let these pass as they might be in the process of being typed into a valid number
          /*  if (!proposedText.equals("-") && !proposedText.matches("-?\\d*\\.?")) {
                return "";  // Block input that results in invalid number format
            }*/
            return  "";
        }
        return null;  // Accept the input as it's within the constraints
    }
}

