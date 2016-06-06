package com.tastybug.timetracker.model.statistics;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.TrackingConfiguration;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class StatisticProjectExpirationTest {

    @Test(expected = NullPointerException.class)
    public void providingNoTrackingConfigurationCausesPreconditionFail() {
        new StatisticProjectExpiration(null);
    }

    @Test(expected = NullPointerException.class)
    public void providingNoCurrentTimeCausesPreconditionFail() {
        new StatisticProjectExpiration(new TrackingConfiguration("project-uuid"), null);
    }

    @Test public void projectWithoutEnddateHasNoExpiration() {
        // given
        TrackingConfiguration configuration = new TrackingConfiguration("project-uuid");
        configuration.setStart(Optional.of(new Date()));
        configuration.setEnd(Optional.<Date>absent());
        StatisticProjectExpiration statisticProjectExpiration = new StatisticProjectExpiration(configuration);

        // when
        Optional<Integer> expirationPercentOpt = statisticProjectExpiration.getExpirationPercent();

        // then
        assertFalse(expirationPercentOpt.isPresent());
    }

    @Test public void projectWithoutStartdateHasNoExpiration() {
        // given
        TrackingConfiguration configuration = new TrackingConfiguration("project-uuid");
        configuration.setStart(Optional.<Date>absent());
        configuration.setEnd(Optional.of(new Date()));
        StatisticProjectExpiration statisticProjectExpiration = new StatisticProjectExpiration(configuration);

        // when
        Optional<Integer> expirationPercentOpt = statisticProjectExpiration.getExpirationPercent();

        // then
        assertFalse(expirationPercentOpt.isPresent());
    }

    @Test public void canCalculateExpirationPercentWithHourPrecision() {
        // given
        TrackingConfiguration configuration = new TrackingConfiguration("project-uuid");
        configuration.setStart(Optional.of(new LocalDateTime(2016, 12, 24, 8, 0).toDate()));
        configuration.setEnd(Optional.of(new LocalDateTime(2016, 12, 25, 8, 0).toDate()));
        Date now = new LocalDateTime(2016, 12, 24, 12, 0).toDate();
        StatisticProjectExpiration statisticProjectExpiration = new StatisticProjectExpiration(configuration, now);

        // when
        Optional<Integer> expirationPercentOpt = statisticProjectExpiration.getExpirationPercent();

        // then: 20h out of 24h equals 16,666% (=16) expiration
        assertEquals(16, (int)expirationPercentOpt.get());
    }

    @Test public void aTimeFrameStartingInTheFutureHasZeroExpiration() {
        // given
        TrackingConfiguration configuration = new TrackingConfiguration("project-uuid");
        configuration.setStart(Optional.of(new LocalDateTime(2016, 12, 24, 8, 0).toDate()));
        configuration.setEnd(Optional.of(new LocalDateTime(2016, 12, 25, 8, 0).toDate()));
        Date now = new LocalDateTime(2016, 12, 23, 12, 0).toDate();
        StatisticProjectExpiration statisticProjectExpiration = new StatisticProjectExpiration(configuration, now);

        // when
        Optional<Integer> expirationPercentOpt = statisticProjectExpiration.getExpirationPercent();

        // then
        assertEquals(0, (int)expirationPercentOpt.get());
    }

    @Test public void aTimeFrameFromThePastHas100PercentExpiration() {
        // given
        TrackingConfiguration configuration = new TrackingConfiguration("project-uuid");
        configuration.setStart(Optional.of(new LocalDateTime(2016, 12, 24, 8, 0).toDate()));
        configuration.setEnd(Optional.of(new LocalDateTime(2016, 12, 25, 8, 0).toDate()));
        Date now = new LocalDateTime(2016, 12, 26, 12, 0).toDate();
        StatisticProjectExpiration statisticProjectExpiration = new StatisticProjectExpiration(configuration, now);

        // when
        Optional<Integer> expirationPercentOpt = statisticProjectExpiration.getExpirationPercent();

        // then
        assertEquals(100, (int)expirationPercentOpt.get());
    }

}