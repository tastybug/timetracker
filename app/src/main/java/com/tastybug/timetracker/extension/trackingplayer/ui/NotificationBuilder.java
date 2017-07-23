package com.tastybug.timetracker.extension.trackingplayer.ui;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.extension.trackingplayer.controller.ButtonIntentHandler;
import com.tastybug.timetracker.infrastructure.util.DefaultLocaleDateFormatter;

import static com.tastybug.timetracker.core.ui.util.DefaultIntentFactory.createOpenProjectDetailsActivityIntent;

class NotificationBuilder {

    private Context context;
    private Notification.Builder notificationBuilder;


    NotificationBuilder(Context context) {
        this.context = context;
        notificationBuilder = new Notification.Builder(context);
    }

    NotificationBuilder forProject(Project project) {
        notificationBuilder.setContentTitle(project.getTitle())
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setOngoing(true)
                .setContentIntent(createOpenProjectDetailsActivityIntent(context, project));

        return this;
    }

    NotificationBuilder withSwitchFromCurrentProject(Project project) {
        notificationBuilder
                .addAction(R.drawable.ic_switch_project,
                        context.getString(R.string.tracking_player_switch_project),
                        createCycleProjectIntent(context, project));
        return this;
    }

    NotificationBuilder forRunningProject(Project project, TrackingRecord trackingRecord) {
        String formattedStart = DefaultLocaleDateFormatter.dateTime().format(trackingRecord.getStart().get());
        notificationBuilder
                .setContentText(context.getString(R.string.tracking_player_tracking_since_X,
                        formattedStart))
                .setSmallIcon(R.drawable.ic_notification_ongoing)
                .addAction(R.drawable.ic_stop_tracking,
                        context.getString(R.string.tracking_player_check_out_button),
                        createStopTrackingIntent(context, project))
                .addAction(R.drawable.ic_pause_tracking,
                        context.getString(R.string.tracking_player_pause_button),
                        createPauseTrackingIntent(context, project));
        return this;
    }

    NotificationBuilder showNotificationForPaused(Project project) {
        Optional<TrackingRecord> latestRecordOpt = new TrackingRecordDAO(context).getLatestByStartDateForProjectUuid(project.getUuid());
        if (latestRecordOpt.isPresent() && latestRecordOpt.get().getEnd().isPresent()) {
            notificationBuilder = notificationBuilder
                    .setContentText(context.getString(R.string.tracking_player_paused_since_X,
                            DefaultLocaleDateFormatter.dateTime().format(latestRecordOpt.get().getEnd().get())));
        } else {
            // even though the project is paused, the latest record might have been deleted manually
            // or altered, so make no assumptions here
            notificationBuilder = notificationBuilder
                    .setContentText(context.getString(R.string.tracking_player_currently_paused));
        }
        notificationBuilder.setSmallIcon(R.drawable.ic_notification_paused)
                .addAction(R.drawable.ic_stop_tracking,
                        context.getString(R.string.tracking_player_dismiss_paused_button),
                        createDismissPausedIntent(context, project))
                .addAction(R.drawable.ic_start_tracking,
                        context.getString(R.string.tracking_player_resume_button),
                        createUnPauseTrackingIntent(context, project))
                .setOngoing(false);

        return this;
    }

    public Notification build() {
        return notificationBuilder.build();
    }


    private static PendingIntent createStopTrackingIntent(Context context, Project affectedProject) {
        return createCallbackIntent(context, affectedProject.getUuid(), ButtonIntentHandler.STOP_TRACKING_PROJECT);
    }

    private static PendingIntent createCycleProjectIntent(Context context, Project currentProject) {
        return createCallbackIntent(context, currentProject.getUuid(), ButtonIntentHandler.CYCLE_TO_NEXT_PROJECT);
    }

    private static PendingIntent createPauseTrackingIntent(Context context, Project currentProject) {
        return createCallbackIntent(context, currentProject.getUuid(), ButtonIntentHandler.PAUSE_TRACKING_PROJECT);
    }

    private static PendingIntent createUnPauseTrackingIntent(Context context, Project currentProject) {
        return createCallbackIntent(context, currentProject.getUuid(), ButtonIntentHandler.UNPAUSE_TRACKING_PROJECT);
    }

    private static PendingIntent createDismissPausedIntent(Context context, Project affectedProject) {
        return createCallbackIntent(context, affectedProject.getUuid(), ButtonIntentHandler.DISMISS_PAUSED_PROJECT);
    }

    private static PendingIntent createCallbackIntent(Context context, String projectUuid, String operation) {
        Intent intent = new Intent(context, ButtonIntentHandler.class)
                .putExtra(ButtonIntentHandler.PROJECT_UUID, projectUuid)
                .putExtra(ButtonIntentHandler.OPERATION, operation);
        return PendingIntent.getService(context, operation.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
