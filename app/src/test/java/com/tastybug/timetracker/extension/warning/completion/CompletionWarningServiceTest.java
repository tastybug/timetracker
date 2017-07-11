package com.tastybug.timetracker.extension.warning.completion;

import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class CompletionWarningServiceTest {

    private CompletionWarningSettings completionWarningSettings
            = mock(CompletionWarningSettings.class);
    private CompletionThresholdViolationIndicator completionThresholdViolationIndicator
            = mock(CompletionThresholdViolationIndicator.class);
    private CompletionNotificationStarter completionNotificationStarter
            = mock(CompletionNotificationStarter.class);

    private CompletionWarningService completionWarningService
            = new CompletionWarningService(completionWarningSettings, completionThresholdViolationIndicator, completionNotificationStarter);

    @Before
    public void setup() {
        when(completionWarningSettings.isEnabled()).thenReturn(true);
    }

    @Test
    public void handleProjectStopped_shows_completion_warning_when_threshold_reached() {
        // given
        when(completionThresholdViolationIndicator.isWarning(anyString())).thenReturn(true);

        // when
        completionWarningService.handleProjectStopped("project-uuid");

        // then
        verify(completionNotificationStarter).showCompletionWarningForProject("project-uuid");
    }

    @Test
    public void handleProjectStopped_shows_no_warning_when_completion_threshold_is_OK() {
        // given
        when(completionThresholdViolationIndicator.isWarning(anyString())).thenReturn(false);

        // when
        completionWarningService.handleProjectStopped("project-uuid");

        // then
        verifyZeroInteractions(completionNotificationStarter);
    }

    @Test
    public void handleProjectStopped_does_nothing_when_disabled() {
        // given
        when(completionWarningSettings.isEnabled()).thenReturn(false);

        // when
        completionWarningService.handleProjectStopped("project-uuid");

        // then
        verifyZeroInteractions(completionThresholdViolationIndicator);
        verifyZeroInteractions(completionNotificationStarter);
    }
}