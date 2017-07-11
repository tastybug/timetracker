package com.tastybug.timetracker.extension.warning.completion;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;

import static com.tastybug.timetracker.core.ui.util.DefaultIntentFactory.createOpenProjectDetailsActivityIntent;

public class CompletionNotificationStarter {

    private static final int COMPLETION_WARNING_INTERNAL_NOTIFICATION_ID
            = "COMPLETION_WARNING_INTERNAL_NOTIFICATION_ID".hashCode();


    private ProjectDAO projectDAO;
    private Context context;
    private Notification.Builder notificationBuilder;

    public CompletionNotificationStarter(Context context) {
        this.context = context;
        this.notificationBuilder = new Notification.Builder(context);
        this.projectDAO = new ProjectDAO(context);
    }

    public void showCompletionWarningForProject(String uuid) {

        Project project = projectDAO.get(uuid).get();

        Notification notification = notificationBuilder.setContentTitle(context.getString(R.string.project_X_completion_warning, project.getTitle()))
                .setContentText(context.getString(R.string.more_than_90_percent_completion_reached))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_notification_warning)
                .setContentIntent(createOpenProjectDetailsActivityIntent(context, project))
                .build();

        getSystemNotificationManager().notify(COMPLETION_WARNING_INTERNAL_NOTIFICATION_ID, notification);
    }

    private NotificationManager getSystemNotificationManager() {
        return (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
    }
}
