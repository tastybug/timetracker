package com.tastybug.timetracker.ui.trackingplayer;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.ui.projectdetails.ProjectDetailsActivity;

public class CallbackIntentFactory {

    public CallbackIntentFactory() {}


    public PendingIntent createOpenProjectDetailsActivityIntent(Context context, Project project) {
        Intent intent = new Intent(context, ProjectDetailsActivity.class)
                .putExtra(ProjectDetailsActivity.PROJECT_UUID, project.getUuid());
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public PendingIntent createStopTrackingIntent(Context context, Project affectedProject) {
        Intent intent = new Intent(context, CallbackService.class)
                .putExtra(CallbackService.PROJECT_UUID, affectedProject.getUuid())
                .putExtra(CallbackService.OPERATION, CallbackService.STOP_TRACKING_PROJECT);
        return PendingIntent.getService(context, CallbackService.STOP_TRACKING_PROJECT.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public PendingIntent createCycleProjectIntent(Context context, Project currentProject) {
        Intent intent = new Intent(context, CallbackService.class)
                .putExtra(CallbackService.PROJECT_UUID, currentProject.getUuid())
                .putExtra(CallbackService.OPERATION, CallbackService.CYCLE_TO_NEXT_PROJECT);
        return PendingIntent.getService(context, CallbackService.CYCLE_TO_NEXT_PROJECT.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
