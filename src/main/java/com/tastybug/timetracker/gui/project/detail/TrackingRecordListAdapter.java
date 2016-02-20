package com.tastybug.timetracker.gui.project.detail;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.gui.view.TrackingRecordView;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.TrackingRecordCreatedEvent;
import com.tastybug.timetracker.task.tracking.TrackingRecordModifiedEvent;

import java.util.ArrayList;
import java.util.Collections;

public class TrackingRecordListAdapter extends BaseAdapter {

    private ArrayList<TrackingRecord> trackingRecordArrayList = new ArrayList<TrackingRecord>();
    private Activity activity;
    private String projectUuid;

    public TrackingRecordListAdapter(Activity activity, String projectUuid) {
        this.activity = activity;
        this.projectUuid = projectUuid;
        readModelFromDatabase();

        new OttoProvider().getSharedBus().register(this);
    }

    private void readModelFromDatabase() {
        trackingRecordArrayList = new TrackingRecordDAO(activity).getByProjectUuid(projectUuid);
        Collections.sort(trackingRecordArrayList);
    }

    @Subscribe public void handleTrackingRecordCreatedEvent(TrackingRecordCreatedEvent event) {
        readModelFromDatabase();
        notifyDataSetChanged();
    }

    @Subscribe public void handleTrackingRecordModifiedEvent(TrackingRecordModifiedEvent event) {
        readModelFromDatabase();
        notifyDataSetChanged();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public int getCount() {
        return trackingRecordArrayList.size();
    }

    public Object getItem(int position) {
        return trackingRecordArrayList.get(position);
    }

    private TrackingRecord getTrackingRecordAt(int position) {
        return (TrackingRecord) getItem(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new TrackingRecordView(activity, null);
        }

        TrackingRecordView trackingRecordView = (TrackingRecordView) convertView;
        trackingRecordView.showTrackingRecord(getTrackingRecordAt(position));

        return trackingRecordView;
    }
}