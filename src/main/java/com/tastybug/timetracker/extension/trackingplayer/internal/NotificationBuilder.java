package com.tastybug.timetracker.extension.trackingplayer.internal;

import android.app.Notification;
import android.content.Context;
import android.graphics.BitmapFactory;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.infrastructure.util.DefaultLocaleDateFormatter;

import static com.tastybug.timetracker.extension.trackingplayer.internal.CallbackIntentFactory.createCycleProjectIntent;
import static com.tastybug.timetracker.extension.trackingplayer.internal.CallbackIntentFactory.createDismissPausedIntent;
import static com.tastybug.timetracker.extension.trackingplayer.internal.CallbackIntentFactory.createOpenProjectDetailsActivityIntent;
import static com.tastybug.timetracker.extension.trackingplayer.internal.CallbackIntentFactory.createPauseTrackingIntent;
import static com.tastybug.timetracker.extension.trackingplayer.internal.CallbackIntentFactory.createStopTrackingIntent;
import static com.tastybug.timetracker.extension.trackingplayer.internal.CallbackIntentFactory.createUnpauseTrackingIntent;

public class NotificationBuilder {

    Context context;
    Notification.Builder notificationBuilder;


    public NotificationBuilder(Context context) {
        this.context = context;
        notificationBuilder = new Notification.Builder(context);
    }

    public NotificationBuilder forProject(Project project) {
        notificationBuilder.setContentTitle(project.getTitle())
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setOngoing(true)
                .setContentIntent(createOpenProjectDetailsActivityIntent(context, project));

        return this;
    }

    public NotificationBuilder withSwitchFromCurrentProject(Project project) {
        notificationBuilder
                .addAction(R.drawable.ic_switch_project,
                        context.getString(R.string.tracking_player_switch_project),
                        createCycleProjectIntent(context, project));
        return this;
    }

    public NotificationBuilder forRunningProject(Project project, TrackingRecord trackingRecord) {
        notificationBuilder
                .setContentText(context.getString(R.string.tracking_player_tracking_since_X,
                        DefaultLocaleDateFormatter.dateTime().format(trackingRecord.getStart().get())))
                .setSmallIcon(R.drawable.ic_notification_ongoing)
                .addAction(R.drawable.ic_stop_tracking,
                        context.getString(R.string.tracking_player_check_out_button),
                        createStopTrackingIntent(context, project))
                .addAction(R.drawable.ic_pause_tracking,
                        context.getString(R.string.tracking_player_pause_button),
                        createPauseTrackingIntent(context, project));
        return this;
    }

    public NotificationBuilder showNotificationForPaused(Project project) {
        TrackingRecord latestRecord = new TrackingRecordDAO(context).getLatestByStartDateForProjectUuid(project.getUuid()).get();

        notificationBuilder
                .setContentText(context.getString(R.string.tracking_player_paused_since_X,
                        DefaultLocaleDateFormatter.dateTime().format(latestRecord.getEnd().get())))
                .setSmallIcon(R.drawable.ic_notification_paused)
                .addAction(R.drawable.ic_stop_tracking,
                        context.getString(R.string.tracking_player_dismiss_paused_button),
                        createDismissPausedIntent(context, project))
                .addAction(R.drawable.ic_start_tracking,
                        context.getString(R.string.tracking_player_resume_button),
                        createUnpauseTrackingIntent(context, project))
                .setOngoing(false);

        return this;
    }

    public Notification build() {
        return notificationBuilder.build();
    }
}
