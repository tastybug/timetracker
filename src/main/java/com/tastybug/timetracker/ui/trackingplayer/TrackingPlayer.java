package com.tastybug.timetracker.ui.trackingplayer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.util.Formatter;

import java.util.ArrayList;

import static com.tastybug.timetracker.ui.trackingplayer.CallbackIntentFactory.createCycleProjectIntent;
import static com.tastybug.timetracker.ui.trackingplayer.CallbackIntentFactory.createDismissPausedIntent;
import static com.tastybug.timetracker.ui.trackingplayer.CallbackIntentFactory.createOpenProjectDetailsActivityIntent;
import static com.tastybug.timetracker.ui.trackingplayer.CallbackIntentFactory.createPauseTrackingIntent;
import static com.tastybug.timetracker.ui.trackingplayer.CallbackIntentFactory.createStopTrackingIntent;
import static com.tastybug.timetracker.ui.trackingplayer.CallbackIntentFactory.createUnpauseTrackingIntent;

public class TrackingPlayer {

    // the ID of the tracking player as the OS' notfication manager requires it
    private static final int TRACKING_PLAYER_INTERNAL_NOTIFICATION_ID = 1;

    private Context context;
    private TrackingPlayerModel model;

    public TrackingPlayer(Context context) {
        this.context = context;
        model = new TrackingPlayerModel(context);
    }

    public void showProject(String projectUuid) {
        Project project = model.getProject(projectUuid);
        if (model.isProjectPaused(projectUuid)) {
            showNotificationForPaused(context, project);
        } else {
            showNotificationForRunning(project, model.getRunningTrackingRecord(projectUuid));
        }
    }

    public void cycleProject(String currentProjectUuid) {
        Project nextProject = model.getNextProject(currentProjectUuid);
        showProject(nextProject.getUuid());
    }

    public void showSomeProjectOrHide() {
        ArrayList<Project> runningAndPausedProjectList = model.getOngoingProjects();
        if (runningAndPausedProjectList.isEmpty()) {
            dismissNotification();
        } else {
            showProject(runningAndPausedProjectList.get(0).getUuid());
        }
    }

    private void showNotificationForRunning(Project project, TrackingRecord trackingRecord) {
        Notification.Builder notificationBuilder = getBasicNotificationBuilder(project)
                .setContentText(context.getString(R.string.tracking_player_tracking_since_X,
                        Formatter.dateTime().format(trackingRecord.getStart().get())))
                .setSmallIcon(R.drawable.ic_notification_ongoing)
                .addAction(R.drawable.ic_stop_tracking,
                        context.getString(R.string.tracking_player_stop_button),
                        createStopTrackingIntent(context, project))
                .addAction(R.drawable.ic_pause_tracking,
                        context.getString(R.string.tracking_player_pause_button),
                        createPauseTrackingIntent(context, project));

        showNotification(notificationBuilder.build());
    }

    private void showNotificationForPaused(Context context, Project project) {
        TrackingRecord latestRecord = new TrackingRecordDAO(context).getLatestByStartDateForProjectUuid(project.getUuid()).get();

        Notification.Builder notificationBuilder = getBasicNotificationBuilder(project)
                .setContentText(context.getString(R.string.tracking_player_paused_since_X,
                        Formatter.dateTime().format(latestRecord.getEnd().get())))
                .setSmallIcon(R.drawable.ic_trackingplayer_paused)
                .addAction(R.drawable.ic_stop_tracking,
                        context.getString(R.string.tracking_player_dismiss_paused_button),
                        createDismissPausedIntent(context, project))
                .addAction(R.drawable.ic_start_tracking,
                        context.getString(R.string.tracking_player_resume_button),
                        createUnpauseTrackingIntent(context, project));

        showNotification(notificationBuilder.build());
    }

    private Notification.Builder getBasicNotificationBuilder(Project project) {
        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentTitle(project.getTitle())
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setOngoing(true)
                .setContentIntent(createOpenProjectDetailsActivityIntent(context, project));

        if (model.getOngoingProjects().size() > 1) {
            notificationBuilder
                    .addAction(R.drawable.ic_switch_project,
                            context.getString(R.string.tracking_player_switch_project),
                            createCycleProjectIntent(context, project));
        }

        return notificationBuilder;
    }

    private void showNotification(Notification notification) {
        getSystemNotificationManager().notify(TRACKING_PLAYER_INTERNAL_NOTIFICATION_ID, notification);
    }

    private void dismissNotification() {
        getSystemNotificationManager().cancel(TRACKING_PLAYER_INTERNAL_NOTIFICATION_ID);
    }

    private NotificationManager getSystemNotificationManager() {
        return (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
    }
}
