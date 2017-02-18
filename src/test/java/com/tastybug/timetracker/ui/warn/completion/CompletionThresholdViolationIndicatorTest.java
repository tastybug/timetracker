package com.tastybug.timetracker.ui.warn.completion;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.statistics.Completion;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompletionThresholdViolationIndicatorTest {

    private ProjectDAO projectDAO = mock(ProjectDAO.class);
    private Project project = mock(Project.class);
    private CompletionStatisticFactory completionStatisticFactory = mock(CompletionStatisticFactory.class);
    private Completion completionBefore = mock(Completion.class);
    private Completion completionAfter = mock(Completion.class);

    private CompletionThresholdViolationIndicator completionThresholdViolationIndicator
            = new CompletionThresholdViolationIndicator(completionStatisticFactory, projectDAO);

    @Before
    public void setup() {
        when(completionStatisticFactory.getCompletionBeforeLastRun(anyString())).thenReturn(completionBefore);
        when(completionStatisticFactory.getCompletionCurrent(anyString())).thenReturn(completionAfter);
        when(projectDAO.get(anyString())).thenReturn(Optional.of(project));
        when(project.isClosed()).thenReturn(false);
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

    @Test
    public void isWarning_returns_false_if_project_is_closed() {
        when(completionBefore.getCompletionPercent()).thenReturn(Optional.of(89d));
        when(completionAfter.getCompletionPercent()).thenReturn(Optional.of(96d));
        when(project.isClosed()).thenReturn(true);

        // when
        boolean completionWarning = completionThresholdViolationIndicator.isWarning("some-uuid");

        // then
        assertFalse(completionWarning);
    }

}