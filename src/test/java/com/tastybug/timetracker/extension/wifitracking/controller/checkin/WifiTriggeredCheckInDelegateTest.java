package com.tastybug.timetracker.extension.wifitracking.controller.checkin;

import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class WifiTriggeredCheckInDelegateTest {

    private TriggerRepository triggerRepository = mock(TriggerRepository.class);
    private ProjectDAO projectDAO = mock(ProjectDAO.class);
    private TrackingRecordDAO trackingRecordDAO = mock(TrackingRecordDAO.class);
    private WifiTriggeredCheckInTask wifiTriggeredCheckInTask = mock(WifiTriggeredCheckInTask.class);

    private WifiTriggeredCheckInDelegate subject = new WifiTriggeredCheckInDelegate(triggerRepository, projectDAO, trackingRecordDAO, wifiTriggeredCheckInTask);

    private String projectUuid = "1";
    private Project project = mock(Project.class);

    @Before
    public void setup() {
        when(wifiTriggeredCheckInTask.withSsidUuid(anyString())).thenReturn(wifiTriggeredCheckInTask);
        when(wifiTriggeredCheckInTask.withProjectUuid(anyString())).thenReturn(wifiTriggeredCheckInTask);
        when(projectDAO.get(projectUuid)).thenReturn(Optional.of(project));
    }

    @Test
    public void handleWifiConnected_does_nothing_when_no_project_is_bound_to_ssid() {
        // given
        when(triggerRepository.getProjectUuidsBoundToSsid(anyString())).thenReturn(Collections.<String>emptyList());

        // when
        subject.handleWifiConnected("some ssid");

        // then
        verifyZeroInteractions(trackingRecordDAO);
        verifyZeroInteractions(wifiTriggeredCheckInTask);
    }

    // just in case a project got deleted somehow without removing the trigger configuration
    @Test
    public void handleWifiConnected_does_nothing_when_project_is_bound_but_project_is_unknown() {
        // given
        when(triggerRepository.getProjectUuidsBoundToSsid(anyString()))
                .thenReturn(Collections.singletonList(projectUuid));
        when(projectDAO.get(projectUuid)).thenReturn(Optional.<Project>absent());

        // when
        subject.handleWifiConnected("some ssid");

        // then
        verifyZeroInteractions(wifiTriggeredCheckInTask);
    }

    @Test
    public void handleWifiConnected_does_nothing_when_project_is_closed() {
        // given
        when(project.isClosed()).thenReturn(true);
        when(triggerRepository.getProjectUuidsBoundToSsid(anyString()))
                .thenReturn(Collections.singletonList(projectUuid));
        when(projectDAO.get(projectUuid)).thenReturn(Optional.<Project>absent());

        // when
        subject.handleWifiConnected("some ssid");

        // then
        verifyZeroInteractions(wifiTriggeredCheckInTask);
    }

    @Test
    public void handleWifiConnected_does_nothing_when_project_is_already_running() {
        // given
        when(project.isClosed()).thenReturn(false);
        when(triggerRepository.getProjectUuidsBoundToSsid(anyString()))
                .thenReturn(Collections.singletonList(projectUuid));
        when(projectDAO.get(projectUuid)).thenReturn(Optional.<Project>absent());
        when(trackingRecordDAO.getRunning(projectUuid)).thenReturn(Optional.of(mock(TrackingRecord.class)));

        // when
        subject.handleWifiConnected("some ssid");

        // then
        verifyZeroInteractions(wifiTriggeredCheckInTask);
    }

    @Test
    public void handleWifiConnected_performs_CheckIn_via_task_for_project() {
        // given
        String ssid = "some ssid";
        when(triggerRepository.getProjectUuidsBoundToSsid(anyString()))
                .thenReturn(Collections.singletonList(projectUuid));
        when(trackingRecordDAO.getRunning(projectUuid)).thenReturn(Optional.<TrackingRecord>absent());

        // when
        subject.handleWifiConnected(ssid);

        // then
        verify(wifiTriggeredCheckInTask).withSsidUuid(ssid);
        verify(wifiTriggeredCheckInTask).withProjectUuid(projectUuid);
        verify(wifiTriggeredCheckInTask).run();
    }
}