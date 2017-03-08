package com.tastybug.timetracker.extension.warning.expiration;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.core.model.statistics.Expiration;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExpirationThresholdViolationIndicatorTest {

    private ProjectDAO projectDAO = mock(ProjectDAO.class);
    private Project project = mock(Project.class);
    private ExpirationStatisticFactory expirationStatisticFactory = mock(ExpirationStatisticFactory.class);
    private Expiration expirationBefore = mock(Expiration.class);
    private Expiration expirationAfter = mock(Expiration.class);

    private ExpirationThresholdViolationIndicator expirationThresholdViolationIndicator
            = new ExpirationThresholdViolationIndicator(expirationStatisticFactory, projectDAO);

    @Before
    public void setup() {
        when(expirationStatisticFactory.getExpirationOnCheckOutOfPreviousSession(anyString())).thenReturn(expirationBefore);
        when(expirationStatisticFactory.getExpirationOnCheckoutOfLastSession(anyString())).thenReturn(expirationAfter);
        when(projectDAO.get(anyString())).thenReturn(Optional.of(project));
        when(project.isClosed()).thenReturn(false);
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

    @Test
    public void isWarning_does_not_warn_if_project_is_closed() {
        // given: the last tracking put the project above the threshold
        when(expirationBefore.getExpirationPercent()).thenReturn(Optional.of(89));
        when(expirationAfter.getExpirationPercent()).thenReturn(Optional.of(96));
        when(project.isClosed()).thenReturn(true);

        // when
        boolean completionWarning = expirationThresholdViolationIndicator.isWarning("some-uuid");

        // then
        assertFalse(completionWarning);
    }

}