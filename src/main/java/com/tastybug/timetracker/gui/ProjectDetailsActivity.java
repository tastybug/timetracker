package com.tastybug.timetracker.gui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.gui.projectdetail.ProjectDetailFragment;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.project.ProjectDeletedEvent;

public class ProjectDetailsActivity extends Activity {

    public static final String PROJECT_UUID = "PROJECT_UUID";

    private String projectUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        setupActionBar();

        if (savedInstanceState != null) {
            projectUuid = savedInstanceState.getString(PROJECT_UUID);
        } else {
            Intent intent = getIntent();
            projectUuid = intent.getStringExtra(PROJECT_UUID);
        }
        setTitle(getProjectByUuid(projectUuid).getTitle());
    }

    @Override
    protected void onResume() {
        super.onResume();

        new OttoProvider().getSharedBus().register(this);

        ProjectDetailFragment detailsFragment = (ProjectDetailFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_project_detail);

        detailsFragment.showProjectDetailsFor(getProjectByUuid(projectUuid));
    }

    @Override
    protected void onPause() {
        super.onPause();
        new OttoProvider().getSharedBus().unregister(this);
    }

    protected Project getProjectByUuid(String uuid) {
        return new ProjectDAO(this).get(uuid).get();
    }

    protected void setupActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // backpress geht hier nicht, da wir nicht notwendigerweise nach 'oben' gingen
                startActivity(new Intent(this, ProjectsActivity.class));
                return true;
            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }

    @Subscribe
    public void handleProjectDeletedEvent(ProjectDeletedEvent event) {
        /*
        Das Eventhandling muss in der Activity passieren, da das Fragment nicht weiss, ob es two-pane oder single-pane
        ausgefuehrt wird. Ergo muss die Activity entscheiden, wie eine Projektloeschung sich navigatorisch auswirkt.
         */
        Toast.makeText(this, "Deleted project " + event.getProjectUuid(), Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

}