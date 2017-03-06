package com.tastybug.timetracker.extensions.checkoutreminder.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tastybug.timetracker.BuildConfig;

import org.joda.time.LocalDateTime;

import java.util.Date;

import static com.tastybug.timetracker.util.ConditionalLog.logInfo;

public class ReminderAlarmSetup {

    private static final int RELEASE_FREQUENCY = 1000 * 60 * 60 * 3;
    private static final int DEBUG_FREQUENCY = 1000 * 20;

    public void setAlarm(Context context) {
        logInfo(ReminderAlarmSetup.class.getSimpleName(), "Arming alarm for checkout reminder on " + getStartDate());

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = getPendingIntent(context);

        cancelStaleAlarm(alarmMgr, pendingIntent);
        startAlarm(alarmMgr, pendingIntent);
    }

    private void cancelStaleAlarm(AlarmManager alarmMgr, PendingIntent pendingIntent) {
        alarmMgr.cancel(pendingIntent);
    }

    private void startAlarm(AlarmManager alarmMgr, PendingIntent pendingIntent) {
        alarmMgr.setInexactRepeating(
                AlarmManager.RTC,
                getStartDate().getTime(),
                getInterval(),
                pendingIntent);
    }

    private PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, ReminderIntentService.class);
        return PendingIntent.getService(context, 0, intent, 0);
    }

    private Date getStartDate() {
        return LocalDateTime.now().plusSeconds(5).toDate();
    }

    private long getInterval() {
        return BuildConfig.DEBUG ? DEBUG_FREQUENCY : RELEASE_FREQUENCY;
    }
}
