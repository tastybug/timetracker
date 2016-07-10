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

    private Context context;
    private TrackingPlayerModel model;

    public TrackingPlayer(Context context) {
        this.context = context;
        model = new TrackingPlayerModel(context);
    }

    public void showRunningProject(TrackingRecord trackingRecord) {
        Project project = new ProjectDAO(context).get(trackingRecord.getProjectUuid()).get();
        showRunningProject(project, trackingRecord);
    }

    private void showRunningProject(Project project, TrackingRecord runningTrackingRecord) {
        showNotification(getNotificationBuilderForRecord(context, project, runningTrackingRecord).build());
    }

    public void showNextProject(String currentProjectUuid) {
        ArrayList<Project> runningProjects = model.getSortedRunningAndPausedProjectList();
        Project nextProject = getNextRunningProject(runningProjects, currentProjectUuid);
        if (model.isProjectPaused(nextProject.getUuid())) {
            showPausedProject(nextProject.getUuid());
        } else {
            showRunningProject(nextProject, new TrackingRecordDAO(context).getRunning(nextProject.getUuid()).get());
        }
    }

    public void revalidateVisibility() {
        ArrayList<Project> runningAndPausedProjectList = model.getSortedRunningAndPausedProjectList();
        if (runningAndPausedProjectList.isEmpty()) {
            dismissNotification();
        } else {
            Project project = runningAndPausedProjectList.get(0);
            if (model.isProjectPaused(project.getUuid())) {
                showPausedProject(project.getUuid());
            } else {
                showRunningProject(project, new TrackingRecordDAO(context).getRunning(project.getUuid()).get());
            }
        }
    }

    public void showPausedProject(String projectUuid) {
        Project project = new ProjectDAO(context).get(projectUuid).get();
        showNotification(getNotificationBuilderForPausedProject(context, project).build());
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
        TrackingRecord latestRecord = new TrackingRecordDAO(context).getLatestByStartDateForProjectUuid(project.getUuid()).get();

        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentTitle(project.getTitle())
                .setContentText(context.getString(R.string.tracking_player_paused_since_X,
                        SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
                                .format(latestRecord.getEnd().get())))
                .setContentIntent(createOpenProjectDetailsActivityIntent(context, project))
                .setSmallIcon(R.drawable.ic_notification_ongoing)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setOngoing(false)
                .addAction(R.drawable.ic_stop_tracking,
                        context.getString(R.string.tracking_player_dismiss_paused_button),
                        createDismissPausedIntent(context, project))
                .addAction(R.drawable.ic_start_tracking,
                        context.getString(R.string.tracking_player_resume_button),
                        createUnpauseTrackingIntent(context, project));

        if (model.getSortedRunningAndPausedProjectList().size() > 1) {
            notificationBuilder
                .addAction(R.drawable.ic_switch_project,
                    context.getString(R.string.tracking_player_switch_project),
                    createCycleProjectIntent(context, project))
                .setOngoing(true); // if multiple projects are ongoing, this must not be dismissable
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
