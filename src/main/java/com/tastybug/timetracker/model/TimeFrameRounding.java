package com.tastybug.timetracker.model;

public class TimeFrameRounding {

    public enum Strategy {
        NO_ROUNDING, FULL_MINUTE_DOWN, FULL_MINUTE_UP, TEN_MINUTES_UP, THIRTY_MINUTES_UP, SIXTY_MINUTES_UP;
    }

}
