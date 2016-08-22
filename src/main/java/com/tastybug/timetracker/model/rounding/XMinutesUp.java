package com.tastybug.timetracker.model.rounding;

import org.joda.time.Duration;

public class XMinutesUp extends RoundingStrategy {

    private int xMinutes;

    protected static XMinutesUp fullMinutesUp() {
        return new XMinutesUp(1);
    }

    protected static XMinutesUp tenMinutesUp() {
        return new XMinutesUp(10);
    }

    protected static XMinutesUp thirtyMinutesUp() {
        return new XMinutesUp(30);
    }

    protected static XMinutesUp fullHoursUp() {
        return new XMinutesUp(60);
    }

    private XMinutesUp(int xMinutes) {
        this.xMinutes = xMinutes;
    }

    @Override
    long getDurationInSeconds(Duration duration) {
        long fullMinutesRoundedUp = getFullMinutesUpInSeconds(duration) / 60;
        return (((fullMinutesRoundedUp / xMinutes) * xMinutes)
                + ((fullMinutesRoundedUp % xMinutes > 0) ? xMinutes : 0)) * 60;
    }

    private long getFullMinutesUpInSeconds(Duration duration) {
        long minutes = duration.getStandardMinutes();
        return (duration.getStandardSeconds() % 60 > 0 ? minutes + 1 : minutes) * 60;
    }
}
