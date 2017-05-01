package com.tastybug.timetracker.core.ui.projectdetails;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;

import java.util.ArrayList;
import java.util.Collections;

class TrackingRecordListAdapter extends BaseAdapter {

    private ArrayList<TrackingRecord> trackingRecordArrayList = new ArrayList<TrackingRecord>();
    private Activity activity;

    TrackingRecordListAdapter(Activity activity, String projectUuid) {
        this.activity = activity;
        rebuildModel(projectUuid);
    }

    void rebuildModel(String projectUuid) {
        trackingRecordArrayList = new TrackingRecordDAO(activity).getByProjectUuid(projectUuid);
        Collections.sort(trackingRecordArrayList);
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