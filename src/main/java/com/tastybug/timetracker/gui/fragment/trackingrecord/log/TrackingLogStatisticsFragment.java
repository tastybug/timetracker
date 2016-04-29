package com.tastybug.timetracker.gui.fragment.trackingrecord.log;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.CreatedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.ModifiedTrackingRecordEvent;

public class TrackingLogStatisticsFragment extends Fragment {

    private TrackingLogStatisticsUI ui;

    private Optional<Project> currentProjectOpt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ui = new TrackingLogStatisticsUI(getActivity());
        return ui.inflateWidgets(inflater, container);
    }

    @Override
    public void onResume() {
        super.onResume();
        new OttoProvider().getSharedBus().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        new OttoProvider().getSharedBus().unregister(this);
    }

    public void showProjectDetailsFor(Project project) {
        this.currentProjectOpt = Optional.of(project);
    }

    public void showNoProject() {
        this.currentProjectOpt = Optional.absent();
    }

    @Subscribe public void handleTrackingStarted(CreatedTrackingRecordEvent event) {
        Project project = new ProjectDAO(getActivity()).get(event.getTrackingRecord().getProjectUuid()).get();
        showProjectDetailsFor(project);
    }

    @Subscribe public void handleTrackingModified(ModifiedTrackingRecordEvent event) {
        Project project = new ProjectDAO(getActivity()).get(event.getTrackingRecord().getProjectUuid()).get();
        showProjectDetailsFor(project);
    }
}
