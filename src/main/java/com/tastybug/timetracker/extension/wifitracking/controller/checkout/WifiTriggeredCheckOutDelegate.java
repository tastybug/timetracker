package com.tastybug.timetracker.extension.wifitracking.controller.checkout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import com.tastybug.timetracker.extension.wifitracking.controller.SessionsLog;

import org.joda.time.LocalDateTime;

import java.util.Date;
import java.util.List;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class WifiTriggeredCheckOutDelegate {

    private Context context;
    private SessionsLog sessionsLog;
    private AlarmManager alarmManager;
    private GracePeriodProvider gracePeriodProvider;

    public WifiTriggeredCheckOutDelegate(Context context) {
        this(context,
                new SessionsLog(context),
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE),
                new GracePeriodProvider(context));
    }

    private WifiTriggeredCheckOutDelegate(Context context,
                                          SessionsLog sessionsLog,
                                          AlarmManager alarmMgr,
                                          GracePeriodProvider gracePeriodProvider) {
        this.context = context;
        this.sessionsLog = sessionsLog;
        this.alarmManager = alarmMgr;
        this.gracePeriodProvider = gracePeriodProvider;
    }

    public void handleWifiDisconnected() {
        List<SessionsLog.Entry> openSessions = sessionsLog.getSessions();
        int gracePeriodInMinutes = gracePeriodProvider.getGracePeriodInSeconds();
        for (SessionsLog.Entry entry: openSessions) {
            entry.setEndDate(new Date());
            sessionsLog.updateSession(entry);

            startGracePeriod(entry, gracePeriodInMinutes);
        }
    }

    private void startGracePeriod(SessionsLog.Entry entry, int gracePeriodLengthSeconds) {
        logInfo(getClass().getSimpleName(), "Arming grace period for %s in %s seconds.", entry.toString(), gracePeriodLengthSeconds + "");

        PendingIntent pendingIntent = new PostGracePeriodCallbackService.IntentFactory().createPendingIntent(
                context,
                entry.getSsid(),
                entry.getTrackingRecordUuid());
        startAlarm(alarmManager, pendingIntent, gracePeriodLengthSeconds);
    }

    private void startAlarm(AlarmManager alarmMgr, PendingIntent pendingIntent, int delaySeconds) {
        alarmMgr.set(AlarmManager.RTC, getStartDate(delaySeconds).getTime(), pendingIntent);
    }

    private Date getStartDate(int delaySeconds) {
        return LocalDateTime.now().plusSeconds(delaySeconds).toDate();
    }

}
