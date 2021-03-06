package jon.happymusicplayer.com.happymusicplayer.utils;

import java.util.Date;
import java.util.Random;

/**
 * Created by Jon on 8/21/2016.
 */
public class Utilities {

    public static int getPercentage(long currValue, long maxValue) {
        Double percentage = (double) 0;
        long currentSeconds = (int) (currValue / 1000);
        long maxSeconds = (int) (maxValue / 1000);
        percentage = (((double) currentSeconds) / maxSeconds) * 100;
        return percentage.intValue();
    }

    public static int getProgressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    public static int getRandomInt(int max, int min) {
        Random rand = new Random();
        return rand.nextInt((max - 1) - min + 1) + min;
    }

    public static int millisToDays(long millis) {
        return (int) millis / (24 * 60 * 60 * 1000);

    }

    public static String getDurationAsText(int millisCurrent, int millisMax) {
        String minMax = formatTimeToText((millisMax / 1000) / 60);
        String secMax = formatTimeToText((millisMax / 1000) % 60);
        String minCurr = formatTimeToText((millisCurrent / 1000) / 60);
        String secCurr = formatTimeToText((millisCurrent / 1000) % 60);

        return minCurr + ":" + secCurr + " / " + minMax + ":" + secMax;
    }

    private static String formatTimeToText(int time) {
        if (time < 10 && time != 0) {
            return "0" + time;
        } else if (time == 0) {
            return "00";
        } else {
            return "" + time;
        }
    }


}
