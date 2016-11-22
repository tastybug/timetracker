package com.tastybug.timetracker.model.rounding;

import org.joda.time.Duration;

class XMinutesUp extends RoundingStrategy {

    private int xMinutes;

    private XMinutesUp(int xMinutes) {
        this.xMinutes = xMinutes;
    }

    static XMinutesUp fullMinutesUp() {
        return new XMinutesUp(1);
    }

    static XMinutesUp tenMinutesUp() {
        return new XMinutesUp(10);
    }

    static XMinutesUp thirtyMinutesUp() {
        return new XMinutesUp(30);
    }

    static XMinutesUp fullHoursUp() {
        return new XMinutesUp(60);
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
