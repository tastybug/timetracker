package com.tastybug.timetracker.extension.wifitracking.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;

import com.google.common.base.Optional;
import com.tastybug.timetracker.extension.wifitracking.controller.checkin.WifiTriggeredCheckInDelegate;
import com.tastybug.timetracker.extension.wifitracking.controller.checkout.WifiTriggeredCheckOutDelegate;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class OSWifiBroadcastReceiver extends BroadcastReceiver {

    private OSConnectivityIndicator osConnectivityIndicator;

    @Override
    public void onReceive(Context context, Intent intent) {
        Optional<WifiInfo> wifiInfo = fetchCurrentWifiInfo(context);
        if (wifiInfo.isPresent()) {
            logInfo(getClass().getSimpleName(), "WIFI Connection: " + wifiInfo.get().getSSID());
            new WifiTriggeredCheckInDelegate(context).handleWifiConnected(wifiInfo.get().getSSID());
        } else {
            logInfo(getClass().getSimpleName(), "WIFI disconnected.");
            new WifiTriggeredCheckOutDelegate(context).handleWifiDisconnected();
        }
    }

    private Optional<WifiInfo> fetchCurrentWifiInfo(Context context) {
        if (osConnectivityIndicator == null) {
            osConnectivityIndicator = new OSConnectivityIndicator(context);
        }
        return osConnectivityIndicator.getWifiConnectionInfo();
    }
}