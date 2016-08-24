package com.tastybug.timetracker.ui.warn.internal.completion;

import android.os.Build;

import com.tastybug.timetracker.ui.warn.completion.CompletionNotificationStarter;
import com.tastybug.timetracker.ui.warn.completion.CompletionThresholdViolationIndicator;
import com.tastybug.timetracker.ui.warn.completion.CompletionWarningService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class CompletionWarningServiceTest {

    CompletionThresholdViolationIndicator completionThresholdViolationIndicator = mock(CompletionThresholdViolationIndicator.class);
    CompletionNotificationStarter completionNotificationStarter = mock(CompletionNotificationStarter.class);

    CompletionWarningService completionWarningService = new CompletionWarningService(completionThresholdViolationIndicator, completionNotificationStarter);

    @Test
    public void handleProjectStopped_shows_completion_warning_when_threshold_reached() {
        // given
        when(completionThresholdViolationIndicator.isWarning(anyString())).thenReturn(true);

        // when
        completionWarningService.handleProjectStopped("project-uuid");

        // then
        verify(completionNotificationStarter, times(1)).showCompletionWarningForProject("project-uuid");
    }

    @Test
    public void handleProjectStopped_shows_no_warning_when_completion_threshold_is_OK() {
        // given
        when(completionThresholdViolationIndicator.isWarning(anyString())).thenReturn(false);

        // when
        completionWarningService.handleProjectStopped("project-uuid");

        // then
        verify(completionNotificationStarter, never()).showCompletionWarningForProject("project-uuid");
    }
}