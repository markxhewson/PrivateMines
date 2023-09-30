package tech.markxhewson.mines.util;

import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static String formatMillis(long loginTime) {
        StringBuilder stringBuilder = new StringBuilder();

        long days = TimeUnit.MILLISECONDS.toDays(loginTime);
        loginTime -= TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS.toHours(loginTime);
        loginTime -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(loginTime);
        loginTime -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(loginTime);

        if (days > 0L) stringBuilder.append(days).append("d");
        if (hours > 0L) stringBuilder.append(" ").append(hours).append("h");
        if (minutes > 0L) stringBuilder.append(" ").append(minutes).append("m");
        if (seconds > 0L) stringBuilder.append(" ").append(seconds).append("s");

        return stringBuilder.toString().trim().equals("") ? "0s" : stringBuilder.toString().trim();
    }

}
