package com.tastybug.timetracker.extension.autoclosure.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.joda.time.LocalDateTime;

import java.util.Date;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

class AlarmSetup {

    void setAlarm(Context context) {
        logInfo(AlarmSetup.class.getSimpleName(), "Arming alarm for auto closure on " + getStartDate());

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = getPendingIntent(context);

        cancelStaleAlarm(alarmMgr, pendingIntent);
        startAlarm(alarmMgr, pendingIntent);
    }

    private void cancelStaleAlarm(AlarmManager alarmMgr, PendingIntent pendingIntent) {
        alarmMgr.cancel(pendingIntent);
    }

    private void startAlarm(AlarmManager alarmMgr, PendingIntent pendingIntent) {
        alarmMgr.setInexactRepeating(AlarmManager.RTC, getStartDate().getTime(),
                getInterval(), pendingIntent);
    }

    private PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, AutoClosureService.class);
        return PendingIntent.getService(context, 0, intent, 0);
    }

    private Date getStartDate() {
        LocalDateTime localDateTime = LocalDateTime.now()
                .withHourOfDay(1)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        if (localDateTime.isBefore(LocalDateTime.now())) {
            localDateTime = localDateTime.plusDays(1);
        }
        return localDateTime.toDate();
    }

    private long getInterval() {
        return AlarmManager.INTERVAL_DAY;
    }
}
