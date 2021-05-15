package org.myongoingscalendar.model;

import java.time.Month;

/**
 * @author firs
 */
public enum Season {
    SPRING, SUMMER, FALL, WINTER;

    static public Season of(Month month) {
        return switch (month) {
            case MARCH, APRIL -> Season.SPRING;
            case MAY, JUNE, JULY, AUGUST -> Season.SUMMER;
            case SEPTEMBER, OCTOBER -> Season.FALL;
            case NOVEMBER, DECEMBER, JANUARY, FEBRUARY -> Season.WINTER;
        };
    }
}