package com.tastybug.timetracker.gui.fragment.trackingrecord.list;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tastybug.timetracker.database.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;

import java.util.ArrayList;
import java.util.Collections;

public class TrackingRecordListAdapter extends BaseAdapter {

    private ArrayList<TrackingRecord> trackingRecordArrayList = new ArrayList<TrackingRecord>();
    private Activity activity;
    private TrackingConfiguration trackingConfiguration;

    public TrackingRecordListAdapter(Activity activity, String projectUuid) {
        this.activity = activity;
        rebuildModel(projectUuid);
    }

    public void rebuildModel(String projectUuid) {
        trackingRecordArrayList = new TrackingRecordDAO(activity).getByProjectUuid(projectUuid);
        trackingConfiguration = new TrackingConfigurationDAO(activity).getByProjectUuid(projectUuid).get();
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
        trackingRecordView.showTrackingRecord(trackingConfiguration, getTrackingRecordAt(position));

        return trackingRecordView;
    }
}