package com.tastybug.timetracker.extension.demodata.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.core.task.project.create.ProjectCreatedEvent;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;

import java.util.List;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class DemoDataCreatedBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = DemoDataCreatedBroadcastReceiver.class.getSimpleName();
    private OttoProvider ottoProvider;

    @Override
    public void onReceive(Context context, Intent intent) {
        logInfo(getClass().getSimpleName(), "Demo data created. Announcing core now.");

        ottoProvider = new OttoProvider();

        showDemoDataCreatedToast(context);
        broadcastDomainEventForCoreUI(context);

    }

    // tell the main UI to render the demo data using 'their' mechanism
    private void broadcastDomainEventForCoreUI(Context context) {
        List<Project> projects = new ProjectDAO(context).getAll();
        if (projects.isEmpty()) {
            logInfo(TAG, "No demo data found to announce.");
        } else {
            ottoProvider.getSharedBus().post(new ProjectCreatedEvent(projects.get(0)));
        }
    }

    private void showDemoDataCreatedToast(Context context) {
        Toast.makeText(context, R.string.toast_demo_data_successfully_created, Toast.LENGTH_SHORT).show();
    }
}
