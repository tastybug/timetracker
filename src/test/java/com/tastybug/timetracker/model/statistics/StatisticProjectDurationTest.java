package com.tastybug.timetracker.model.statistics;

import com.tastybug.timetracker.model.TimeFrame;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.rounding.RoundingFactory;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class StatisticProjectDurationTest {

    @Test(expected = NullPointerException.class)
    public void creatingStatisticsForNullTrackingConfigurationYieldsException() {
        new StatisticProjectDuration(null, new ArrayList<TimeFrame>());
    }

    @Test(expected = NullPointerException.class)
    public void creatingStatisticsForNullTimeFrameArrayYieldsException() {
        new StatisticProjectDuration(new TrackingConfiguration("some project uuid"), null);
    }

    @Test public void canCreateStatisticForEmptyListOfTimeFrames() {
        // given
        ArrayList<TimeFrame> list = new ArrayList<TimeFrame>();
        TrackingConfiguration configuration = aTrackingConfigurationWithNoRounding();

        // when
        Duration duration = new StatisticProjectDuration(configuration, list).get();

        // then
        assertEquals(0, duration.getStandardSeconds());
    }

    @Test public void canCalculateWhenNoRoundingIsConfigured() {
        // given
        ArrayList<TimeFrame> list = new ArrayList<TimeFrame>();
        TrackingConfiguration configuration = aTrackingConfigurationWithNoRounding();

        // when
        Duration duration = new StatisticProjectDuration(configuration, list).get();

        // then
        assertEquals(0, duration.getStandardSeconds());
    }

    @Test public void canCalculateEffectiveProjectDurationWithNoRounding() {
        // given
        ArrayList<TimeFrame> timeFrames = timeFramesList(aTimeFrameWith10Min10SecsDuration(), aTimeFrameWith10Min10SecsDuration());
        TrackingConfiguration configuration = aTrackingConfigurationWithNoRounding();

        // when
        Duration duration = new StatisticProjectDuration(configuration, timeFrames).get();

        // then: 2 * 10:10min
        assertEquals(2*(10*60+10), duration.getStandardSeconds());
    }

    @Test public void canCalculateEffectiveProjectDurationWith10erRounding() {
        // given
        ArrayList<TimeFrame> timeFrames = timeFramesList(aTimeFrameWith10Min10SecsDuration(), aTimeFrameWith10Min10SecsDuration());
        TrackingConfiguration configuration = aTrackingConfigurationWith10erRounding();

        // when
        Duration duration = new StatisticProjectDuration(configuration, timeFrames).get();

        // then: 2 * 20 minutes
        assertEquals(2*20*60, duration.getStandardSeconds());
    }

    /*
        The calculation will ignore those time frames that are ongoing
     */
    @Test public void calculationIgnoresIncompleteTimeFrames() {
        // given
        ArrayList<TimeFrame> timeFrames = timeFramesList(aTimeFrameWith10Min10SecsDuration(), anOngoingTimeFrame(), aTimeFrameWith10Min10SecsDuration());
        TrackingConfiguration configuration = aTrackingConfigurationWithNoRounding();

        // when
        Duration duration = new StatisticProjectDuration(configuration, timeFrames).get();

        // then
        assertEquals(2*(10*60+10), duration.getStandardSeconds());
    }

    private TrackingConfiguration aTrackingConfigurationWithNoRounding() {
        return new TrackingConfiguration("some project uuid", RoundingFactory.Strategy.NO_ROUNDING);
    }

    private TrackingConfiguration aTrackingConfigurationWith10erRounding() {
        return new TrackingConfiguration("some project uuid", RoundingFactory.Strategy.TEN_MINUTES_UP);
    }

    private ArrayList<TimeFrame> timeFramesList(TimeFrame... timeFrames) {

        return new ArrayList<TimeFrame>(Arrays.asList(timeFrames));
    }

    private TimeFrame aTimeFrameWith10Min10SecsDuration() {
        DateTime start = new DateTime(2015, 1, 1, 13, 30, 0);
        DateTime stop = new DateTime(2015, 1, 1, 13, 40, 10);

        return new TimeFrame("uuid", "some project uuid", start.toDate(), stop.toDate(), "some description");
    }

    private TimeFrame anOngoingTimeFrame() {
        DateTime start = new DateTime(2015, 1, 1, 13, 30, 0);

        return new TimeFrame("uuid", "some project uuid", start.toDate(), null, "some description");
    }
}