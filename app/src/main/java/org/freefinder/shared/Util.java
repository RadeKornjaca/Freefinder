package org.freefinder.shared;

/**
 * Created by rade on 27.8.17..
 */

public class Util {
    private static final int START_IDX = 0;
    private static final int END_IDX = 50;

    public static String descriptionShortener(String description) {
        return description.substring(START_IDX,
                                     description.length() < END_IDX ? description.length() : END_IDX).concat("...");
    }
}
