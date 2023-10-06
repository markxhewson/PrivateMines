package tech.markxhewson.mines.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class NumberUtil {

    private static String[] arr = new String[]{"", "k", "m", "b", "t", "p", "e"};

    public static String format(double value) {
        int index = 0;

        while ((value / 1000) >= 1) {
            value = value / 1000;
            index++;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return String.format("%s%s", decimalFormat.format(value), arr[index]);
    }
}
