package com.tastybug.timetracker.extension.autoclosure.controller;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.statistics.Expiration;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClosabilityIndicatorTest {

    private ExpirationFactory expirationFactory = mock(ExpirationFactory.class);
    private ClosabilityIndicator closabilityIndicator = new ClosabilityIndicator(expirationFactory);

    @Test
    public void isProjectClosable_returns_false_when_project_is_already_closed() {
        Project project = mock(Project.class);
        when(project.isClosed()).thenReturn(true);

        boolean result = closabilityIndicator.isProjectClosable(project);

        assertFalse(result);
    }

    @Test
    public void isProjectClosable_returns_false_when_project_is_not_expired_yet() {
        Project project = mock(Project.class);
        when(project.isClosed()).thenReturn(false);
        Expiration expiration = mock(Expiration.class);
        when(expiration.isExpired()).thenReturn(false);
        when(expirationFactory.createExpiration(project)).thenReturn(expiration);

        boolean result = closabilityIndicator.isProjectClosable(project);

        assertFalse(result);
    }

    @Test
    public void isProjectClosable_returns_true_when_project_is_expired() {
        Project project = mock(Project.class);
        when(project.isClosed()).thenReturn(false);
        Expiration expiration = mock(Expiration.class);
        when(expiration.isExpired()).thenReturn(true);
        when(expirationFactory.createExpiration(project)).thenReturn(expiration);

        boolean result = closabilityIndicator.isProjectClosable(project);

        assertTrue(result);
    }

}