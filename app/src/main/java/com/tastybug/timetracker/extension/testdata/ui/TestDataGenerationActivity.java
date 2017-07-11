package com.tastybug.timetracker.extension.testdata.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.ui.dashboard.ProjectsActivity;
import com.tastybug.timetracker.extension.testdata.controller.TestDataGeneratedEvent;
import com.tastybug.timetracker.extension.testdata.controller.TestDataGenerationTask;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;

public class TestDataGenerationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.test_data_generation_activity_title);

        showConfirmationDialog();
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Dialog dialog = builder.setMessage(getString(R.string.confirm_test_data_creation_dialog_msg))
                .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplication(), R.string.toast_starting_test_data_generator, Toast.LENGTH_SHORT).show();
                        new TestDataGenerationTask(getApplicationContext()).run();
                    }
                })
                .setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showProjectsActivity();
                    }
                }).create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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
    public void handleTestdataCreated(TestDataGeneratedEvent event) {
        showProjectsActivity();
    }

    public void showProjectsActivity() {
        Intent intent = new Intent(getApplicationContext(), ProjectsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
    }
}
