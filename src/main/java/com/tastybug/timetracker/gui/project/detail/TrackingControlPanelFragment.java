package com.tastybug.timetracker.gui.project.detail;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.facade.TrackingFacade;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.TrackingRecordCreatedEvent;
import com.tastybug.timetracker.task.tracking.TrackingRecordModifiedEvent;

public class TrackingControlPanelFragment extends Fragment implements View.OnClickListener {

    private TextView someTextView;
    private Project currentProject;
    private ImageButton trackingStartStopButton;

    @Override
    public void onDetach() {
        super.onDetach();
        new OttoProvider().getSharedBus().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_tracking_control_panel, container);

        someTextView = (TextView) rootview.findViewById(R.id.someTextview);
        trackingStartStopButton = (ImageButton) rootview.findViewById(R.id.trackingStartStop);
        trackingStartStopButton.setOnClickListener(this);

        new OttoProvider().getSharedBus().register(this);

        return rootview;
    }

    public void showProject(Project project) {
        this.currentProject = project;
        Optional<TrackingRecord> ongoingTracking = new TrackingFacade(getActivity()).getOngoingTracking(project.getUuid());
        if(ongoingTracking.isPresent()) {
            visualizeOngoingTracking(ongoingTracking.get());
        } else {
            visualizeNoOngoingTracking();
        }
    }

    private void visualizeOngoingTracking(TrackingRecord ongoingTrackingRecord) {
        trackingStartStopButton.setImageResource(R.drawable.ic_stop_tracking);
        someTextView.setText("Already tracking since " + ongoingTrackingRecord.getStart().get());
    }

    private void visualizeNoOngoingTracking() {
        trackingStartStopButton.setImageResource(R.drawable.ic_start_tracking);
        someTextView.setText("Ready to track!");
    }

    private void visualizeNoProjectSelected() {
        someTextView.setText("//Nothing selected for tracking control panel");
    }

    public void showNoProject() {
        this.currentProject = null;
        visualizeNoProjectSelected();
    }

    public void onClick(View v) {
        if (currentProject == null) {
            Toast.makeText(getActivity(), R.string.message_no_project_selected, Toast.LENGTH_SHORT).show();
            return;
        }
        TrackingFacade facade = new TrackingFacade(getActivity());
        Optional<TrackingRecord> ongoing = facade.getOngoingTracking(currentProject.getUuid());
        if (ongoing.isPresent()) {
            facade.stopTracking(currentProject.getUuid());
        } else {
            facade.startTracking(currentProject.getUuid());
        }
    }

    @Subscribe public void handleTrackingStarted(TrackingRecordCreatedEvent event) {
        visualizeOngoingTracking(event.getTrackingRecord());
    }

    @Subscribe public void handleTrackingModified(TrackingRecordModifiedEvent event) {
        if (!new TrackingFacade(getActivity()).getOngoingTracking(currentProject.getUuid()).isPresent()) {
            visualizeNoOngoingTracking();
        }
    }
}
