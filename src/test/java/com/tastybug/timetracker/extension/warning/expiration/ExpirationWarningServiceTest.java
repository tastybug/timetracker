package com.tastybug.timetracker.extension.warning.expiration;

import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class ExpirationWarningServiceTest {

    private ExpirationThresholdViolationIndicator expirationThresholdViolationIndicator
            = mock(ExpirationThresholdViolationIndicator.class);
    private ExpirationNotificationStarter expirationNotificationStarter
            = mock(ExpirationNotificationStarter.class);

    private ExpirationWarningService warningService
            = new ExpirationWarningService(expirationThresholdViolationIndicator, expirationNotificationStarter);

    @Test
    public void handleProjectStopped_shows_expiration_warning_when_threshold_reached() {
        // given
        when(expirationThresholdViolationIndicator.isWarning(anyString())).thenReturn(true);

        // when
        warningService.handleProjectStopped("project-uuid");

        // then
        verify(expirationNotificationStarter).showExpirationWarningForProject("project-uuid");
    }

    @Test
    public void handleProjectStopped_shows_no_warning_when_expiration_threshold_is_OK() {
        // given
        when(expirationThresholdViolationIndicator.isWarning(anyString())).thenReturn(false);

        // when
        warningService.handleProjectStopped("project-uuid");

        // then
        verify(expirationNotificationStarter, never()).showExpirationWarningForProject("project-uuid");
    }

}