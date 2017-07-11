package com.tastybug.timetracker.extension.wifitracking.controller.checkout;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.extension.wifitracking.controller.OSConnectivityIndicator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class PostGracePeriodCallbackServiceTest {

    private static final String TRACKING_RECORD_UUID_KEY = "TRACKING_RECORD_UUID_KEY";
    private static final String SSID_KEY = "SSID_KEY";

    private WifiTriggeredCheckOutTask wifiTriggeredCheckOutTask = mock(WifiTriggeredCheckOutTask.class);
    private OSConnectivityIndicator osConnectivityIndicator = mock(OSConnectivityIndicator.class);
    private TrackingRecordDAO trackingRecordDAO = mock(TrackingRecordDAO.class);
    private WifiInfo wifiInfo = mock(WifiInfo.class);

    private PostGracePeriodCallbackService subject = new PostGracePeriodCallbackService(osConnectivityIndicator, trackingRecordDAO, wifiTriggeredCheckOutTask);

    @Before
    public void setup() {
        when(wifiTriggeredCheckOutTask.withTrackingRecordUuid(anyString())).thenReturn(wifiTriggeredCheckOutTask);
        when(wifiInfo.getSSID()).thenReturn("Some default SSID");
    }

    // Testcase: during the grace period, the tracking record that has been created by
    // Wifi-Tracking has been deleted. Nothing to be done.
    @Test
    public void onHandleIntent_does_NOT_checkout_if_the_WiFi_TrackingRecord_has_been_deleted_during_grace() {
        // given
        String trackingRecordUuid = "1234";
        when(trackingRecordDAO.get(trackingRecordUuid)).thenReturn(Optional.<TrackingRecord>absent());

        // when
        subject.onHandleIntent(anIntent(trackingRecordUuid, "some ssid"));

        // then
        verifyZeroInteractions(wifiTriggeredCheckOutTask);
    }

    // Testcase: during the grace period, the tracking record has been stopped manually.
    // Nothing to be done.
    @Test
    public void onHandleIntent_does_NOT_checkout_if_the_WiFi_TrackingRecord_has_been_stopped_manually() {
        // given
        String trackingRecordUuid = "1234";
        TrackingRecord trackingRecord = mock(TrackingRecord.class);
        when(trackingRecord.isRunning()).thenReturn(false);
        when(trackingRecordDAO.get(trackingRecordUuid)).thenReturn(Optional.of(trackingRecord));

        // when
        subject.onHandleIntent(anIntent(trackingRecordUuid, "some ssid"));

        // then
        verifyZeroInteractions(wifiTriggeredCheckOutTask);
    }

    // Testcase: during the grace period the WiFi connection has been reestablished. The tracking
    // continues as before.
    @Test
    public void onHandleIntent_does_NOT_checkout_if_the_original_WiFi_connection_has_been_reestablished() {
        // given
        String trackingRecordUuid = "1234";
        String ssid = "ssid";
        TrackingRecord trackingRecord = mock(TrackingRecord.class);
        when(trackingRecord.isRunning()).thenReturn(true);
        when(trackingRecordDAO.get(trackingRecordUuid)).thenReturn(Optional.of(trackingRecord));
        when(wifiInfo.getSSID()).thenReturn(ssid);
        when(osConnectivityIndicator.getWifiConnectionInfo()).thenReturn(Optional.of(wifiInfo));

        // when
        subject.onHandleIntent(anIntent(trackingRecordUuid, ssid));

        // then
        verifyZeroInteractions(wifiTriggeredCheckOutTask);
    }

    // During the grace period, wifi connectivity has been established with a DIFFERENT network.
    // CheckOut is in order.
    @Test
    public void onHandleIntent_DOES_checkout_when_Wifi_connection_with_different_network_is_present() {
        // given
        String trackingRecordUuid = "1234";
        TrackingRecord trackingRecord = mock(TrackingRecord.class);
        when(trackingRecord.isRunning()).thenReturn(true);
        when(trackingRecordDAO.get(trackingRecordUuid)).thenReturn(Optional.of(trackingRecord));
        when(wifiInfo.getSSID()).thenReturn("SOME OTHER SSID");
        when(osConnectivityIndicator.getWifiConnectionInfo()).thenReturn(Optional.of(wifiInfo));

        // when
        subject.onHandleIntent(anIntent(trackingRecordUuid, "original SSID"));

        // then
        verify(wifiTriggeredCheckOutTask).withTrackingRecordUuid(trackingRecordUuid);
        verify(wifiTriggeredCheckOutTask).run();
    }

    private Intent anIntent(String trackingRecordUuid, String originalSsid) {
        Intent intent = mock(Intent.class);
        when(intent.getStringExtra(TRACKING_RECORD_UUID_KEY)).thenReturn(trackingRecordUuid);
        when(intent.getStringExtra(SSID_KEY)).thenReturn(originalSsid);
        return intent;
    }

}