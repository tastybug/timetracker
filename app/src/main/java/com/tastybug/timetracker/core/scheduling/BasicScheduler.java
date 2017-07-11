package com.tastybug.timetracker.core.scheduling;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import com.tastybug.timetracker.BuildConfig;

import org.joda.time.LocalDateTime;

import java.util.Date;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public abstract class BasicScheduler {

    public void setAlarm(Context context) {
        logInfo(BasicScheduler.class.getSimpleName(), "Scheduling '%s' for %s", getAlarmTopic(), getStartDate().toString());

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

    protected abstract String getAlarmTopic();

    protected abstract PendingIntent getPendingIntent(Context context);

    protected Date getStartDate() {
        return BuildConfig.DEBUG ? getDebugStartTime() : getReleaseStartTime();
    }

    protected Date getDebugStartTime() {
        return LocalDateTime.now().plusSeconds(10).toDate();
    }

    protected Date getReleaseStartTime() {
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

    protected long getInterval() {
        return BuildConfig.DEBUG ? getDebugFrequency() : getReleaseFrequency();
    }

    protected abstract long getReleaseFrequency();

    protected long getDebugFrequency() {
        return 1000 * 60 * 60;
    }
}
