package com.tastybug.timetracker.model;

import android.widget.Toast;

import com.tastybug.timetracker.util.VersionUtil;

import net.danlew.android.joda.JodaTimeAndroid;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initializeJoda();

        printWelcomeToast();
    }

    private void initializeJoda() {
        JodaTimeAndroid.init(this);
    }

    private void printWelcomeToast() {
        Toast.makeText(this, new VersionUtil(this).getVersionName(), Toast.LENGTH_SHORT).show();
    }
}
