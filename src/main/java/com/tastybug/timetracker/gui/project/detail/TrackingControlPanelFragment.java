package com.tastybug.timetracker.gui.project.detail;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.CreatedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.KickStartTrackingRecordTask;
import com.tastybug.timetracker.task.tracking.KickStartedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.KickStopTrackingRecordTask;
import com.tastybug.timetracker.task.tracking.KickStoppedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.ModifiedTrackingRecordEvent;
import com.tastybug.timetracker.util.DurationFormatterFactory;

import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.util.Date;

public class TrackingControlPanelFragment extends Fragment implements View.OnClickListener {

    private TextView lineOne, lineTwo;
    private ImageButton trackingStartStopButton;
    private Handler uiUpdateHandler = new Handler();

    private Optional<Project> currentProjectOpt;
    private Optional<TrackingRecord> ongoingTrackingRecordOpt = Optional.absent();

    @Override
    public void onDetach() {
        super.onDetach();
        new OttoProvider().getSharedBus().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_tracking_control_panel, container);

        lineOne = (TextView) rootview.findViewById(R.id.lineOne);
        lineTwo = (TextView) rootview.findViewById(R.id.lineTwo);
        trackingStartStopButton = (ImageButton) rootview.findViewById(R.id.trackingStartStop);
        trackingStartStopButton.setOnClickListener(this);

        new OttoProvider().getSharedBus().register(this);

        return rootview;
    }

    public void showProject(Project project) {
        this.currentProjectOpt = Optional.of(project);
        Optional<TrackingRecord> ongoingTracking = new TrackingRecordDAO(getActivity()).getRunning(project.getUuid());
        if(ongoingTracking.isPresent()) {
            visualizeOngoingTracking(ongoingTracking);
        } else {
            visualizeNoOngoingTracking();
        }
    }

    private void visualizeOngoingTracking(Optional<TrackingRecord> ongoingTrackingRecord) {
        this.ongoingTrackingRecordOpt = ongoingTrackingRecord;
        trackingStartStopButton.setImageResource(R.drawable.ic_stop_tracking);

        lineOne.setText(getString(R.string.msg_tracking_since_X,
                getStartDateAsString(ongoingTrackingRecord.get().getStart().get())));
        lineTwo.setText(getString(R.string.msg_tracking_duration_X,
                getDurationAsString(ongoingTrackingRecord.get().toDuration().get())));
    }

    private String getStartDateAsString(Date startDate) {
        DateFormat startDateFormatter;
        if (new LocalDate().isEqual(new LocalDate(startDate))) {
            // today
            startDateFormatter = DateFormat.getTimeInstance(DateFormat.MEDIUM);
        } else {
            // yesterday or even farther away
            startDateFormatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        }
        return startDateFormatter.format(startDate);
    }

    private String getDurationAsString(Duration duration) {
        return DurationFormatterFactory.getFormatter(getActivity(), duration).print(duration.toPeriod());
    }

    private void visualizeNoOngoingTracking() {
        this.ongoingTrackingRecordOpt = Optional.absent();
        trackingStartStopButton.setImageResource(R.drawable.ic_start_tracking);
        lineOne.setText(R.string.msg_no_ongoing_tracking);
        lineTwo.setText("");
    }

    public void onClick(View v) {
        if (!currentProjectOpt.isPresent()) {
            Toast.makeText(getActivity(), R.string.message_no_project_selected, Toast.LENGTH_SHORT).show();
            return;
        }
        String projectUuid = currentProjectOpt.get().getUuid();
        Optional<TrackingRecord> ongoing = new TrackingRecordDAO(getActivity()).getRunning(projectUuid);
        if (ongoing.isPresent()) {
            KickStopTrackingRecordTask.aTask(getActivity()).withProjectUuid(projectUuid).execute();
        } else {
            KickStartTrackingRecordTask.aTask(getActivity()).withProjectUuid(projectUuid).execute();
        }
    }

    @Subscribe public void handleTrackingCreated(CreatedTrackingRecordEvent event) {
        showProject(new ProjectDAO(getActivity()).get(event.getTrackingRecord().getProjectUuid()).get());
    }

    @Subscribe public void handleTrackingKickStarted(KickStartedTrackingRecordEvent event) {
        visualizeOngoingTracking(Optional.of(event.getTrackingRecord()));
    }

    @Subscribe public void handleTrackingModified(ModifiedTrackingRecordEvent event) {
        if(currentProjectOpt.isPresent()) {
            if (!new TrackingRecordDAO(getActivity()).getRunning(currentProjectOpt.get().getUuid()).isPresent()) {
                visualizeNoOngoingTracking();
            }
        }
    }

    @Subscribe public void handleTrackingKickStopped(KickStoppedTrackingRecordEvent event) {
        if(currentProjectOpt.isPresent()) {
            if (!new TrackingRecordDAO(getActivity()).getRunning(currentProjectOpt.get().getUuid()).isPresent()) {
                visualizeNoOngoingTracking();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiUpdateHandler.removeCallbacks(updateUITask);
        uiUpdateHandler.postDelayed(updateUITask, 100);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiUpdateHandler.removeCallbacks(updateUITask);
    }

    private Runnable updateUITask = new Runnable() {
        public void run() {
            if(TrackingControlPanelFragment.this.ongoingTrackingRecordOpt.isPresent()) {
                visualizeOngoingTracking(TrackingControlPanelFragment.this.ongoingTrackingRecordOpt);
            }
            uiUpdateHandler.postDelayed(updateUITask, 1000);
        }
    };
}
