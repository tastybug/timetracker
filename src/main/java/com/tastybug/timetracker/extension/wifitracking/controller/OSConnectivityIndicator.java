package com.tastybug.timetracker.extension.wifitracking.controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.google.common.base.Optional;

public class OSConnectivityIndicator {

    private Context context;

    public OSConnectivityIndicator(Context context) {
        this.context = context;
    }

    public Optional<WifiInfo> getWifiConnectionInfo() {
        ConnectivityManager conMan = getConnectivityManager(context);
        if (isConnectedToWifi(conMan)) {
            return Optional.of(getWifiManager(context).getConnectionInfo());
        } else {
            return Optional.absent();
        }
    }

    private boolean isConnectedToWifi(ConnectivityManager connectivityManager) {
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI && netInfo.isConnected();
    }

    private ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private WifiManager getWifiManager(Context context) {
        return (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

}
