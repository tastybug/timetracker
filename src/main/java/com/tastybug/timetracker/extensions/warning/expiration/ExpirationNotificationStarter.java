package com.tastybug.timetracker.extensions.warning.expiration;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.util.DefaultLocaleDateFormatter;

import java.util.Date;

import static com.tastybug.timetracker.extensions.trackingplayer.internal.CallbackIntentFactory.createOpenProjectDetailsActivityIntent;

public class ExpirationNotificationStarter {

    private static final int EXPIRATION_WARNING_INTERNAL_NOTIFICATION_ID
            = "EXPIRATION_WARNING_INTERNAL_NOTIFICATION_ID".hashCode();

    private ProjectDAO projectDAO;
    private TrackingConfigurationDAO trackingConfigurationDAO;
    private Context context;
    private Notification.Builder notificationBuilder;

    public ExpirationNotificationStarter(Context context) {
        this.context = context;
        this.notificationBuilder = new Notification.Builder(context);
        this.projectDAO = new ProjectDAO(context);
        this.trackingConfigurationDAO = new TrackingConfigurationDAO(context);
    }

    public void showExpirationWarningForProject(String uuid) {

        Project project = projectDAO.get(uuid).get();
        Date endDate = trackingConfigurationDAO.getByProjectUuid(uuid).get().getEndDateAsInclusive().get();

        Notification notification = notificationBuilder.setContentTitle(context.getString(R.string.project_X_expiration_warning, project.getTitle()))
                .setContentText(context.getString(R.string.project_ends_on_X, DefaultLocaleDateFormatter.date().format(endDate)))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_notification_warning)
                .setContentIntent(createOpenProjectDetailsActivityIntent(context, project))
                .build();

        getSystemNotificationManager().notify(EXPIRATION_WARNING_INTERNAL_NOTIFICATION_ID, notification);
    }

    private NotificationManager getSystemNotificationManager() {
        return (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
    }
}
