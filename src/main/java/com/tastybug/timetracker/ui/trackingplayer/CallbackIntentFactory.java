package com.tastybug.timetracker.ui.trackingplayer;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.ui.projectdetails.ProjectDetailsActivity;

public class CallbackIntentFactory {

    private CallbackIntentFactory() {}


    public static PendingIntent createOpenProjectDetailsActivityIntent(Context context, Project project) {
        Intent intent = new Intent(context, ProjectDetailsActivity.class)
                .putExtra(ProjectDetailsActivity.PROJECT_UUID, project.getUuid());
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent createStopTrackingIntent(Context context, Project affectedProject) {
        Intent intent = new Intent(context, CallbackService.class)
                .putExtra(CallbackService.PROJECT_UUID, affectedProject.getUuid())
                .putExtra(CallbackService.OPERATION, CallbackService.STOP_TRACKING_PROJECT);
        return PendingIntent.getService(context, CallbackService.STOP_TRACKING_PROJECT.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent createStartTrackingIntent(Context context, Project affectedProject) {
        Intent intent = new Intent(context, CallbackService.class)
                .putExtra(CallbackService.PROJECT_UUID, affectedProject.getUuid())
                .putExtra(CallbackService.OPERATION, CallbackService.START_TRACKING_PROJECT);
        return PendingIntent.getService(context, CallbackService.START_TRACKING_PROJECT.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent createCycleProjectIntent(Context context, Project currentProject) {
        Intent intent = new Intent(context, CallbackService.class)
                .putExtra(CallbackService.PROJECT_UUID, currentProject.getUuid())
                .putExtra(CallbackService.OPERATION, CallbackService.CYCLE_TO_NEXT_PROJECT);
        return PendingIntent.getService(context, CallbackService.CYCLE_TO_NEXT_PROJECT.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent createPauseTrackingIntent(Context context, Project currentProject) {
        Intent intent = new Intent(context, CallbackService.class)
                .putExtra(CallbackService.PROJECT_UUID, currentProject.getUuid())
                .putExtra(CallbackService.OPERATION, CallbackService.PAUSE_TRACKING_PROJECT);
        return PendingIntent.getService(context, CallbackService.PAUSE_TRACKING_PROJECT.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent createUnpauseTrackingIntent(Context context, Project currentProject) {
        Intent intent = new Intent(context, CallbackService.class)
                .putExtra(CallbackService.PROJECT_UUID, currentProject.getUuid())
                .putExtra(CallbackService.OPERATION, CallbackService.UNPAUSE_TRACKING_PROJECT);
        return PendingIntent.getService(context, CallbackService.UNPAUSE_TRACKING_PROJECT.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    public static PendingIntent createDismissPausedIntent(Context context, Project affectedProject) {
        Intent intent = new Intent(context, CallbackService.class)
                .putExtra(CallbackService.PROJECT_UUID, affectedProject.getUuid())
                .putExtra(CallbackService.OPERATION, CallbackService.DISMISS_PAUSED_PROJECT);
        return PendingIntent.getService(context, CallbackService.DISMISS_PAUSED_PROJECT.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
