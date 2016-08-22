package com.tastybug.timetracker.ui.trackingplayer.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.ui.projectdetails.ProjectDetailsActivity;
import com.tastybug.timetracker.ui.trackingplayer.CallbackService;

public class CallbackIntentFactory {

    private CallbackIntentFactory() {
    }


    public static PendingIntent createOpenProjectDetailsActivityIntent(Context context, Project project) {
        Intent intent = new Intent(context, ProjectDetailsActivity.class)
                .putExtra(ProjectDetailsActivity.PROJECT_UUID, project.getUuid());
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent createStopTrackingIntent(Context context, Project affectedProject) {
        return createCallbackIntent(context, affectedProject.getUuid(), CallbackService.STOP_TRACKING_PROJECT);
    }

    public static PendingIntent createCycleProjectIntent(Context context, Project currentProject) {
        return createCallbackIntent(context, currentProject.getUuid(), CallbackService.CYCLE_TO_NEXT_PROJECT);
    }

    public static PendingIntent createPauseTrackingIntent(Context context, Project currentProject) {
        return createCallbackIntent(context, currentProject.getUuid(), CallbackService.PAUSE_TRACKING_PROJECT);
    }

    public static PendingIntent createUnpauseTrackingIntent(Context context, Project currentProject) {
        return createCallbackIntent(context, currentProject.getUuid(), CallbackService.UNPAUSE_TRACKING_PROJECT);
    }

    public static PendingIntent createDismissPausedIntent(Context context, Project affectedProject) {
        return createCallbackIntent(context, affectedProject.getUuid(), CallbackService.DISMISS_PAUSED_PROJECT);
    }

    private static PendingIntent createCallbackIntent(Context context, String projectUuid, String operation) {
        Intent intent = new Intent(context, CallbackService.class)
                .putExtra(CallbackService.PROJECT_UUID, projectUuid)
                .putExtra(CallbackService.OPERATION, operation);
        return PendingIntent.getService(context, operation.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
