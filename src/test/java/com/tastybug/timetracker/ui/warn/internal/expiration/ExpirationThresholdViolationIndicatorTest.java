package com.tastybug.timetracker.ui.warn.internal.expiration;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.statistics.Expiration;
import com.tastybug.timetracker.ui.warn.expiration.ExpirationStatisticFactory;
import com.tastybug.timetracker.ui.warn.expiration.ExpirationThresholdViolationIndicator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExpirationThresholdViolationIndicatorTest {

    ExpirationStatisticFactory expirationStatisticFactory = mock(ExpirationStatisticFactory.class);
    Expiration expirationBefore = mock(Expiration.class);
    Expiration expirationAfter = mock(Expiration.class);

    ExpirationThresholdViolationIndicator expirationThresholdViolationIndicator = new ExpirationThresholdViolationIndicator(expirationStatisticFactory);

    @Before
    public void setup() {
        when(expirationStatisticFactory.getExpirationOnCheckOutOfPreviousSession(anyString())).thenReturn(expirationBefore);
        when(expirationStatisticFactory.getExpirationOnCheckoutOfLastSession(anyString())).thenReturn(expirationAfter);
    }

    @Test
    public void isWarning_warns_when_90_percent_was_just_reached() {
        // given: the last tracking put the project above the threshold
        when(expirationBefore.getExpirationPercent()).thenReturn(Optional.of(89));
        when(expirationAfter.getExpirationPercent()).thenReturn(Optional.of(90));

        // when
        boolean completionWarning = expirationThresholdViolationIndicator.isWarning("some-uuid");

        // then
        assertTrue(completionWarning);
    }

    @Test
    public void isWarning_does_not_warn_below_90_percent() {
        // given: was and still is below threshold
        when(expirationBefore.getExpirationPercent()).thenReturn(Optional.of(88));
        when(expirationAfter.getExpirationPercent()).thenReturn(Optional.of(89));

        // when
        boolean completionWarning = expirationThresholdViolationIndicator.isWarning("some-uuid");

        // then
        assertFalse(completionWarning);
    }

    @Test
    public void isWarning_does_not_warn_when_no_completion_calculatable() {
        // given
        when(expirationBefore.getExpirationPercent()).thenReturn(Optional.<Integer>absent());
        when(expirationAfter.getExpirationPercent()).thenReturn(Optional.<Integer>absent());

        // when
        boolean completionWarning = expirationThresholdViolationIndicator.isWarning("some-uuid");

        // then
        assertFalse(completionWarning);
    }

}