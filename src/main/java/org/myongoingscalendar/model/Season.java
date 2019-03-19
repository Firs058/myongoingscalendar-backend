package org.myongoingscalendar.model;

import java.time.Month;

/**
 * @author firs
 */
public enum Season {
    SPRING, SUMMER, FALL, WINTER;

    static public Season of(Month month) {
        switch (month) {
            case MARCH:
                return Season.SPRING;
            case APRIL:
                return Season.SPRING;
            case MAY:
                return Season.SUMMER;
            case JUNE:
                return Season.SUMMER;
            case JULY:
                return Season.SUMMER;
            case AUGUST:
                return Season.SUMMER;
            case SEPTEMBER:
                return Season.FALL;
            case OCTOBER:
                return Season.FALL;
            case NOVEMBER:
                return Season.WINTER;
            case DECEMBER:
                return Season.WINTER;
            case JANUARY:
                return Season.WINTER;
            case FEBRUARY:
                return Season.WINTER;
            default:
                throw new IllegalArgumentException();
        }
    }
}