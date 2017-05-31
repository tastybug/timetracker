package com.tastybug.timetracker.core.ui.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.ui.projectdetails.ProjectDetailsActivity;

public class DefaultIntentFactory {

    private DefaultIntentFactory() {
    }

    public static PendingIntent createOpenProjectDetailsActivityIntent(Context context, Project project) {
        Intent intent = new Intent(context, ProjectDetailsActivity.class)
                .putExtra(ProjectDetailsActivity.PROJECT_UUID, project.getUuid());
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
