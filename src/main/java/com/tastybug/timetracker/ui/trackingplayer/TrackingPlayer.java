package com.tastybug.timetracker.ui.trackingplayer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import static com.tastybug.timetracker.ui.trackingplayer.CallbackIntentFactory.createCycleProjectIntent;
import static com.tastybug.timetracker.ui.trackingplayer.CallbackIntentFactory.createDismissPausedIntent;
import static com.tastybug.timetracker.ui.trackingplayer.CallbackIntentFactory.createOpenProjectDetailsActivityIntent;
import static com.tastybug.timetracker.ui.trackingplayer.CallbackIntentFactory.createPauseTrackingIntent;
import static com.tastybug.timetracker.ui.trackingplayer.CallbackIntentFactory.createStopTrackingIntent;
import static com.tastybug.timetracker.ui.trackingplayer.CallbackIntentFactory.createUnpauseTrackingIntent;

public class TrackingPlayer {

    // the ID of the tracking player as the OS' notfication manager requires it
    private static final int TRACKING_PLAYER_INTERNAL_NOTIFICATION_ID = 1;

    private TrackingPlayerModel model;

    public TrackingPlayer(Context context) {
        model = new TrackingPlayerModel(context);
    }

    public void showRunningProject(Context context, TrackingRecord trackingRecord) {
        Project project = new ProjectDAO(context).get(trackingRecord.getProjectUuid()).get();
        showRunningProject(context, project, trackingRecord);
    }

    private void showRunningProject(Context context, Project project, TrackingRecord runningTrackingRecord) {
        showNotification(context, getNotificationBuilderForRecord(context, project, runningTrackingRecord).build());
    }

    public void showNextProject(Context context, String currentProjectUuid) {
        ArrayList<Project> runningProjects = model.getSortedRunningAndPausedProjectList();
        Project nextProject = getNextRunningProject(runningProjects, currentProjectUuid);
        if (model.isProjectPaused(nextProject.getUuid())) {
            showPausedProject(context, nextProject.getUuid());
        } else {
            showRunningProject(context, nextProject, new TrackingRecordDAO(context).getRunning(nextProject.getUuid()).get());
        }
    }

    public void revalidateVisibility(Context context) {
        ArrayList<Project> runningAndPausedProjectList = model.getSortedRunningAndPausedProjectList();
        if (runningAndPausedProjectList.isEmpty()) {
            dismissNotification(context);
        } else {
            Project project = runningAndPausedProjectList.get(0);
            if (model.isProjectPaused(project.getUuid())) {
                showPausedProject(context, project.getUuid());
            } else {
                showRunningProject(context, project, new TrackingRecordDAO(context).getRunning(project.getUuid()).get());
            }
        }
    }

    public void showPausedProject(Context context, String projectUuid) {
        Project project = new ProjectDAO(context).get(projectUuid).get();
        showNotification(context, getNotificationBuilderForPausedProject(context, project).build());
    }

    private Notification.Builder getNotificationBuilderForRecord(Context context, Project project, TrackingRecord trackingRecord) {
        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentTitle(project.getTitle())
                .setContentText(context.getString(R.string.tracking_player_tracking_since_X,
                        SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT).format(trackingRecord.getStart().get())))
                .setContentIntent(createOpenProjectDetailsActivityIntent(context, project))
                .setSmallIcon(R.drawable.ic_notification_ongoing)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setOngoing(true)
                .addAction(R.drawable.ic_stop_tracking,
                        context.getString(R.string.tracking_player_stop_button),
                        createStopTrackingIntent(context, project))
                .addAction(R.drawable.ic_pause_tracking,
                        context.getString(R.string.tracking_player_pause_button),
                        createPauseTrackingIntent(context, project));

        if (model.getSortedRunningAndPausedProjectList().size() > 1) {
            notificationBuilder.addAction(R.drawable.ic_switch_project,
                    context.getString(R.string.tracking_player_switch_project),
                    createCycleProjectIntent(context, project));
        }
        return notificationBuilder;
    }

    private Notification.Builder getNotificationBuilderForPausedProject(Context context, Project project) {
        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentTitle(project.getTitle())
                .setContentText(context.getString(R.string.paused_project))
                .setContentIntent(createOpenProjectDetailsActivityIntent(context, project))
                .setSmallIcon(R.drawable.ic_notification_ongoing)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setOngoing(true)
                .addAction(R.drawable.ic_start_tracking,
                        context.getString(R.string.tracking_player_resume_button),
                        createUnpauseTrackingIntent(context, project))
                .addAction(R.drawable.ic_stop_tracking,
                        context.getString(R.string.tracking_player_dismiss_paused_button),
                        createDismissPausedIntent(context, project));

        if (model.getSortedRunningAndPausedProjectList().size() > 1) {
            notificationBuilder.addAction(R.drawable.ic_switch_project,
                    context.getString(R.string.tracking_player_switch_project),
                    createCycleProjectIntent(context, project));
        }
        return notificationBuilder;
    }

    private void showNotification(Context context, Notification notification) {
        getSystemNotificationManager(context).notify(TRACKING_PLAYER_INTERNAL_NOTIFICATION_ID, notification);
    }

    private void dismissNotification(Context context) {
        getSystemNotificationManager(context).cancel(TRACKING_PLAYER_INTERNAL_NOTIFICATION_ID);
    }

    private NotificationManager getSystemNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
    }

    private Project getNextRunningProject(ArrayList<Project> projects, String previousProjectUuid) {
        for (Iterator<Project> i = projects.iterator(); i.hasNext();) {
            if (i.next().getUuid().equals(previousProjectUuid)) {
                return i.hasNext() ? i.next() : projects.get(0);
            }
        }
        // if the previous project is not in the list, just return the very first entry
        return projects.get(0);
    }
}
