package com.diahelp.tools;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by AlparslanSelçuk on 17.06.2017.
 */

public class Number {

    public static String format(Double number) {
        NumberFormat formatter = new DecimalFormat("#0.00");
        String numberString = formatter.format(number);
        return numberString.contains(",00") ? numberString.replace(",00", "") : numberString;
    }
}
