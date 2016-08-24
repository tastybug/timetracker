package com.tastybug.timetracker.ui.warn.internal.completion;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.statistics.StatisticProjectCompletion;
import com.tastybug.timetracker.ui.warn.completion.CompletionStatisticFactory;
import com.tastybug.timetracker.ui.warn.completion.CompletionThresholdViolationIndicator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompletionThresholdViolationIndicatorTest {

    CompletionStatisticFactory completionStatisticFactory = mock(CompletionStatisticFactory.class);
    StatisticProjectCompletion completionBefore = mock(StatisticProjectCompletion.class);
    StatisticProjectCompletion completionAfter = mock(StatisticProjectCompletion.class);

    CompletionThresholdViolationIndicator completionThresholdViolationIndicator = new CompletionThresholdViolationIndicator(completionStatisticFactory);

    @Before
    public void setup() {
        when(completionStatisticFactory.getCompletionBeforeLastRun(anyString())).thenReturn(completionBefore);
        when(completionStatisticFactory.getCompletionCurrent(anyString())).thenReturn(completionAfter);
    }

    @Test
    public void isWarning_warns_when_90_percent_was_just_reached() {
        // given: the last tracking put the project above the threshold
        when(completionBefore.getCompletionPercent()).thenReturn(Optional.of(89d));
        when(completionAfter.getCompletionPercent()).thenReturn(Optional.of(90d));

        // when
        boolean completionWarning = completionThresholdViolationIndicator.isWarning("some-uuid");

        // then
        assertTrue(completionWarning);
    }

    @Test
    public void isWarning_does_not_warn_below_90_percent() {
        // given: was and still is below threshold
        when(completionBefore.getCompletionPercent()).thenReturn(Optional.of(88d));
        when(completionAfter.getCompletionPercent()).thenReturn(Optional.of(89d));

        // when
        boolean completionWarning = completionThresholdViolationIndicator.isWarning("some-uuid");

        // then
        assertFalse(completionWarning);
    }

    @Test
    public void isWarning_does_not_warn_when_no_completion_calculatable() {
        // given
        when(completionBefore.getCompletionPercent()).thenReturn(Optional.<Double>absent());
        when(completionAfter.getCompletionPercent()).thenReturn(Optional.<Double>absent());

        // when
        boolean completionWarning = completionThresholdViolationIndicator.isWarning("some-uuid");

        // then
        assertFalse(completionWarning);
    }

}