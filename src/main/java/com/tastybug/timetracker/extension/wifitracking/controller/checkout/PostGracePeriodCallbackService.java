package com.tastybug.timetracker.extension.wifitracking.controller.checkout;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.extension.wifitracking.controller.OSConnectivityIndicator;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class PostGracePeriodCallbackService extends IntentService {

    private static final String TRACKING_RECORD_UUID_KEY = "TRACKING_RECORD_UUID_KEY";
    private static final String SSID_KEY = "SSID_KEY";

    private WifiTriggeredCheckOutTask wifiTriggeredCheckOutTask;
    private OSConnectivityIndicator osConnectivityIndicator;
    private TrackingRecordDAO trackingRecordDAO;

    public PostGracePeriodCallbackService() {
        super(PostGracePeriodCallbackService.class.getSimpleName());
    }

    // tests only
    PostGracePeriodCallbackService(OSConnectivityIndicator osConnectivityIndicator,
                                   TrackingRecordDAO trackingRecordDAO,
                                   WifiTriggeredCheckOutTask wifiTriggeredCheckOutTask) {
        super(PostGracePeriodCallbackService.class.getSimpleName());
        this.osConnectivityIndicator = osConnectivityIndicator;
        this.trackingRecordDAO = trackingRecordDAO;
        this.wifiTriggeredCheckOutTask = wifiTriggeredCheckOutTask;
    }

    private void initializeCollaborators() {
        if (wifiTriggeredCheckOutTask == null) {
            wifiTriggeredCheckOutTask = new WifiTriggeredCheckOutTask(getApplicationContext());
        }
        if (osConnectivityIndicator == null) {
            osConnectivityIndicator = new OSConnectivityIndicator(getApplicationContext());
        }
        if (trackingRecordDAO == null) {
            trackingRecordDAO = new TrackingRecordDAO(getApplicationContext());
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        initializeCollaborators();
        handleGracePeriodPassed(intent.getStringExtra(SSID_KEY), intent.getStringExtra(TRACKING_RECORD_UUID_KEY));
    }

    void handleGracePeriodPassed(String originalSsid, String trackingRecordUuid) {
        logInfo(getClass().getSimpleName(), "Wifi disconnect grace period over..");
        Optional<TrackingRecord> trackingRecord = trackingRecordDAO.get(trackingRecordUuid);
        if (!trackingRecord.isPresent()) {
            logInfo(getClass().getSimpleName(), ".. tracking record not found, nothing to be done.");
        } else if (!trackingRecord.get().isRunning()) {
            logInfo(getClass().getSimpleName(), ".. tracking record not running anymore, nothing to be done: %s", trackingRecord.get().toString());
        } else if (isConnectionReestablished(originalSsid)) {
            logInfo(getClass().getSimpleName(), ".. connection has been re-established, nothing to be done.");
        } else {
            logInfo(getClass().getSimpleName(), ".. tracking record will be stopped now: %s.", trackingRecord.get().toString());
            wifiTriggeredCheckOutTask
                    .withTrackingRecordUuid(trackingRecordUuid)
                    .run();
        }
    }

    private boolean isConnectionReestablished(String originalSsid) {
        Optional<WifiInfo> infoOptional = osConnectivityIndicator.getWifiConnectionInfo();
        return infoOptional.isPresent() && infoOptional.get().getSSID().equals(originalSsid);
    }

    static class IntentFactory {

        PendingIntent createPendingIntent(Context context, String ssid, String trackingRecordUuid) {
            Intent intent = new Intent(context, PostGracePeriodCallbackService.class);
            intent.putExtra(PostGracePeriodCallbackService.TRACKING_RECORD_UUID_KEY, trackingRecordUuid);
            intent.putExtra(PostGracePeriodCallbackService.SSID_KEY, ssid);
            return PendingIntent.getService(context, 0, intent, 0);
        }

    }
}