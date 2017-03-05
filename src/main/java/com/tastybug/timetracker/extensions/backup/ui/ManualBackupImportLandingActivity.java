package com.tastybug.timetracker.extensions.backup.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.extensions.backup.controller.dataimport.ImportDataTask;
import com.tastybug.timetracker.extensions.backup.controller.dataimport.ImportedDataEvent;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
import com.tastybug.timetracker.ui.dashboard.ProjectsActivity;

import static com.tastybug.timetracker.util.ConditionalLog.logInfo;

public class ManualBackupImportLandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_landing_activity);

        logInfo(getClass().getSimpleName(), "Received intent for data: " + getIntent().getData() + ", scheme: " + getIntent().getScheme() + ", type: " + getIntent().getType());
        showWarningDialog();
    }

    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.import_data_dialog_title)
                .setMessage(R.string.import_data_dialog_message)
                .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplication(), R.string.data_import_starting, Toast.LENGTH_LONG).show();
                        new ImportDataTask(getApplication()).withDataUri(getIntent().getData()).run();
                    }
                })
                .setNegativeButton(R.string.common_discard, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showProjectsActivity();
                    }
                });
        builder.create().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        new OttoProvider().getSharedBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        new OttoProvider().getSharedBus().unregister(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleDataImported(ImportedDataEvent event) {
        Toast.makeText(getApplication(), R.string.data_import_done, Toast.LENGTH_SHORT).show();
        showProjectsActivity();
    }

    public void showProjectsActivity() {
        Intent intent = new Intent(getApplicationContext(), ProjectsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
    }
}
