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

public class TrackingRecordListAdapter extends BaseAdapter {

    private ArrayList<TrackingRecord> trackingRecordArrayList = new ArrayList<TrackingRecord>();
    private Activity activity;

    public TrackingRecordListAdapter(Activity activity, String projectUuid) {
        trackingRecordArrayList = new TrackingRecordDAO(activity).getByProjectUuid(projectUuid);
        this.activity = activity;

        new OttoProvider().getSharedBus().register(this);
    }

    @Subscribe
    public void handleTrackingRecordCreatedEvent(TrackingRecordCreatedEvent event) {
        this.trackingRecordArrayList.add(event.getTrackingRecord());
        this.notifyDataSetChanged();
    }

    @Subscribe
    public void handleTrackingRecordModifiedEvent(TrackingRecordModifiedEvent event) {
        updateTrackingRecordEntity(event.getTrackingRecord());
        notifyDataSetChanged();
    }

    private void updateTrackingRecordEntity(TrackingRecord newerEntity) {
        for (int i = 0; i < getCount(); i++) {
            if (getTrackingRecordAt(i).getUuid().equals(newerEntity.getUuid())) {
                trackingRecordArrayList.remove(i);
                trackingRecordArrayList.add(i, newerEntity);
                break;
            }
        }
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