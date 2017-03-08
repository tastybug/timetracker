package com.tastybug.timetracker.core.model.rounding;

import org.joda.time.DateTime;
import org.joda.time.Duration;

public class AbstractRoundingTestcase {

    protected Duration getOneMinute10SecondsDuration() {
        DateTime start = new DateTime(2015, 1, 1, 13, 30, 0);
        DateTime stop = new DateTime(2015, 1, 1, 13, 31, 10);

        return new Duration(start, stop);
    }

    protected Duration get1MinuteDuration() {
        DateTime start = new DateTime(2015, 1, 1, 13, 30, 0);
        DateTime stop = new DateTime(2015, 1, 1, 13, 31, 0);

        return new Duration(start, stop);
    }

    protected Duration get10SecondsDuration() {
        DateTime start = new DateTime(2015, 1, 1, 13, 30, 0);
        DateTime stop = new DateTime(2015, 1, 1, 13, 30, 10);

        return new Duration(start, stop);
    }
}
