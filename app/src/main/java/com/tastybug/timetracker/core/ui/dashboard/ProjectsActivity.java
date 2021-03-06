package com.tastybug.timetracker.core.ui.dashboard;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.ui.delegate.CheckoutAskForDescriptionDelegate;

public class ProjectsActivity extends AppCompatActivity {

    private CheckoutAskForDescriptionDelegate checkoutAskForDescriptionDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        setTitle(R.string.activity_project_title);
        setupActionBar();
    }

    protected void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false); // disables UP arrow
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkoutAskForDescriptionDelegate = new CheckoutAskForDescriptionDelegate(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkoutAskForDescriptionDelegate.stop();
    }
}