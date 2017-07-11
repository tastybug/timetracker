package com.tastybug.timetracker.infrastructure.runtime;

import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.infrastructure.filecache.CacheCleaner;

import net.danlew.android.joda.JodaTimeAndroid;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import static org.acra.ReportField.ANDROID_VERSION;
import static org.acra.ReportField.APP_VERSION_CODE;
import static org.acra.ReportField.APP_VERSION_NAME;
import static org.acra.ReportField.PHONE_MODEL;
import static org.acra.ReportField.STACK_TRACE;

@ReportsCrashes(
        mailTo = "tastybug@tastybug.com",
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.acra_crash_toast_message, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
        resDialogTitle = R.string.acra_dialog_title,
        resDialogText = R.string.acra_dialog_text,
        resDialogIcon = R.mipmap.ic_launcher,
        resDialogPositiveButtonText = R.string.acra_button_send_mail,
        resDialogNegativeButtonText = R.string.acra_button_cancel,
        resDialogOkToast = R.string.acra_contribution_thank,
        resDialogTheme = R.style.Theme_AppCompat_DayNight_Dialog,
        customReportContent = { APP_VERSION_NAME, APP_VERSION_CODE, ANDROID_VERSION, PHONE_MODEL, STACK_TRACE}
)
public class Application extends android.app.Application {

    private FirstRunHelper firstRunHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        firstRunHelper = new FirstRunHelper(this);

        initializeJoda();
        initializeDayNightThemeMode();
        cleanupCacheFolder();
        broadcastAppStart();
        if (isFirstRun()) {
            broadcastFirstRun();
            declareFirstRunConsumed();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }

    private void initializeJoda() {
        JodaTimeAndroid.init(this);
    }

    private void broadcastAppStart() {
        getApplicationContext().sendBroadcast(new AppStartPropagationIntent());
    }

    private void broadcastFirstRun() {
        getApplicationContext().sendBroadcast(new FirstRunPropagationIntent());
    }

    private void initializeDayNightThemeMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    private void cleanupCacheFolder() {
        new CacheCleaner(this).cleanupCache();
    }

    private boolean isFirstRun() {
        return firstRunHelper.isFirstRun();
    }

    private void declareFirstRunConsumed() {
        firstRunHelper.declareFirstRunConsumed();
    }
}
