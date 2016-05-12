package com.tastybug.timetracker.gui.fragment.project.listng;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.tastybug.timetracker.R;

public class ProjectListFabs extends BottomSheetBehavior.BottomSheetCallback {

    private Animation fadeinFabAnimation,
            fadeoutFabAnimation;
    private FloatingActionButton createProjectFab,
            startTrackingFab,
            stopTrackingFab,
            deleteProjectFab;


    public ProjectListFabs(Activity activity, View rootView, ProjectDetailsBottomSheet projectDetailsBottomSheet) {
        projectDetailsBottomSheet.registerBehaviourCallback(this);
        prepareAnimations(activity);
        initFabs(activity, rootView);
    }

    private void prepareAnimations(Activity activity) {
        fadeinFabAnimation = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fab_open);
        fadeoutFabAnimation = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.fab_close);
    }

    private void initFabs(final Activity activity, View rootView) {
        createProjectFab = (FloatingActionButton)rootView.findViewById(R.id.create_project_fab);
        startTrackingFab = (FloatingActionButton)rootView.findViewById(R.id.start_tracking_fab);
        stopTrackingFab = (FloatingActionButton)rootView.findViewById(R.id.stop_tracking_fab);
        deleteProjectFab = (FloatingActionButton)rootView.findViewById(R.id.delete_project_fab);

        createProjectFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "create project", Toast.LENGTH_SHORT).show();
            }
        });
        startTrackingFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "start tracking", Toast.LENGTH_SHORT).show();
            }
        });
        stopTrackingFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "stop tracking", Toast.LENGTH_SHORT).show();
            }
        });
        deleteProjectFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "delete project", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showProjectContextFabs(){
        createProjectFab.startAnimation(fadeoutFabAnimation);
        startTrackingFab.startAnimation(fadeinFabAnimation);
        deleteProjectFab.startAnimation(fadeinFabAnimation);
        startTrackingFab.setClickable(true);
        deleteProjectFab.setClickable(true);
    }

    public void showGeneralFabs() {
        createProjectFab.startAnimation(fadeinFabAnimation);
        startTrackingFab.startAnimation(fadeoutFabAnimation);
        deleteProjectFab.startAnimation(fadeoutFabAnimation);
        startTrackingFab.setClickable(false);
        deleteProjectFab.setClickable(false);

    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
        switch(newState) {
            case BottomSheetBehavior.STATE_EXPANDED:
                showProjectContextFabs();
                return;
            case BottomSheetBehavior.STATE_COLLAPSED:
                showGeneralFabs();
                return;
        }
    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

    }
}
