package com.edonoxako.geophoto.app;

import android.content.Context;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class InputValidator {

    public static final String DATE_REGEX = "(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d";

    private ArrayList<Control> texts;
    private ArrayList<Control> dates;
    private ArrayList<Control> numbers;

    private boolean isValidated = true;
    private Context context;

    public InputValidator(Context context) {
        this.context = context;
    }

    public void addTexts(EditText text) {
        if (texts == null) {
            texts = new ArrayList<Control>();
        }
        Control c = new Control();
        c.control = text;
        texts.add(c);
    }

    public void addNumbers(EditText number) {
        if (numbers == null) {
            numbers = new ArrayList<Control>();
        }
        Number n = new Number();
        n.control = number;
        numbers.add(n);
    }

    public void addNumbers(EditText number, double minValue, double maxValue) {
        if (numbers == null) {
           numbers = new ArrayList<Control>();
        }
        Number n = new Number();
        n.control = number;
        n.range = new double[] {minValue, maxValue};
        numbers.add(n);
    }

    public void addDates(EditText date) {
        if (dates == null) {
            dates = new ArrayList<Control>();
        }
        Control c =  new Control();
        c.control = date;
        dates.add(c);
    }

    public boolean validate() {
        if (texts != null) {
            checkForEmptiness(texts);
        }

        if (numbers != null) {
            checkForEmptiness(numbers);
            checkForRange();
        }

        if (dates != null) {
            checkForEmptiness(dates);
            checkDateFormat();
        }

        return isValidated;
    }

    private void checkForEmptiness(List<Control> controls) {
        for (Control c : controls) {
            if (c.control.getText().toString().isEmpty()) {
                c.control.setError(context.getString(R.string.empty_text_error_msg));
                isValidated = false;
            }
        }
    }

    private void checkForRange() {
        for (Control control : numbers) {
            Number number = (Number) control;
            if (number.range != null) {
                double min = number.range[0];
                double max = number.range[1];
                double num = Double.valueOf(number.control.getText().toString());
                if (num < min || num > max) {
                    number.control.setError(context.getString(R.string.number_out_of_range_error_msg));
                    isValidated = false;
                }
            }
        }

    }

    private void checkDateFormat() {
        for (Control c : dates) {
            Pattern pattern = Pattern.compile(DATE_REGEX);
            Matcher matcher = pattern.matcher(c.control.getText().toString());
            if (!matcher.matches()) {
                c.control.setError(context.getString(R.string.wrong_date_error_msg));
                isValidated = false;
            }
        }
    }


    private class Control {
        EditText control;
    }

    private class Number extends Control {
        double[] range = null;
    }
}
