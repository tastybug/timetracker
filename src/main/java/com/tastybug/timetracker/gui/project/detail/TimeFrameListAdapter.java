package com.tastybug.timetracker.gui.project.detail;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.database.dao.TimeFrameDAO;
import com.tastybug.timetracker.gui.view.TimeFrameView;
import com.tastybug.timetracker.model.TimeFrame;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.TimeFrameCreatedEvent;
import com.tastybug.timetracker.task.tracking.TimeFrameModifiedEvent;

import java.util.ArrayList;

public class TimeFrameListAdapter extends BaseAdapter {

    private ArrayList<TimeFrame> timeFrameArrayList = new ArrayList<TimeFrame>();
    private Activity activity;

    public TimeFrameListAdapter(Activity activity, String projectUuid) {
        timeFrameArrayList = new TimeFrameDAO(activity).getByProjectUuid(projectUuid);
        this.activity = activity;

        new OttoProvider().getSharedBus().register(this);
    }

    @Subscribe
    public void handleTimeFrameCreatedEvent(TimeFrameCreatedEvent event) {
        this.timeFrameArrayList.add(event.getTimeFrame());
        this.notifyDataSetChanged();
    }

    @Subscribe
    public void handleTimeFrameModifiedEvent(TimeFrameModifiedEvent event) {
        updateTimeFrameEntity(event.getTimeFrame());
        notifyDataSetChanged();
    }

    private void updateTimeFrameEntity(TimeFrame newerEntity) {
        for (int i = 0; i < getCount(); i++) {
            if (getTimeFrameAt(i).getUuid().equals(newerEntity.getUuid())) {
                timeFrameArrayList.remove(i);
                timeFrameArrayList.add(i, newerEntity);
                break;
            }
        }
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public int getCount() {
        return timeFrameArrayList.size();
    }

    public Object getItem(int position) {
        return timeFrameArrayList.get(position);
    }

    private TimeFrame getTimeFrameAt(int position) {
        return (TimeFrame) getItem(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new TimeFrameView(activity, null);
        }

        TimeFrameView timeFrameView = (TimeFrameView) convertView;
        timeFrameView.showTimeFrame(getTimeFrameAt(position));

        return timeFrameView;
    }
}