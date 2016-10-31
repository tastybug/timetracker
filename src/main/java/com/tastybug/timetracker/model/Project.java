package com.tastybug.timetracker.model;

import android.content.Context;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;

import java.util.ArrayList;
import java.util.UUID;

public class Project extends Entity implements Comparable<Project> {

    private String uuid = UUID.randomUUID().toString();
    private String title;
    private Optional<String> description = Optional.absent();
    private TrackingConfiguration trackingConfiguration;
    private ArrayList<TrackingRecord> trackingRecords;


    public Project(String uuid, String title, Optional<String> description) {
        this.uuid = uuid;
        this.title = title;
        this.description = description;
    }

    public Project(String title) {
        this.title = title;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        Preconditions.checkNotNull(uuid);
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        Preconditions.checkArgument(title != null && title.length() > 0, "Project has empty or null title!");
        this.title = title;
    }

    public Optional<String> getDescription() {
        return description;
    }

    public void setDescription(Optional<String> description) {
        Preconditions.checkNotNull(description);
        this.description = description;
    }

    public ArrayList<TrackingRecord> getTrackingRecords() {
        return trackingRecords;
    }

    public void setTrackingRecords(ArrayList<TrackingRecord> trackingRecords) {
        this.trackingRecords = trackingRecords;
    }

    public ArrayList<TrackingRecord> getTrackingRecords(Context context) {
        Preconditions.checkNotNull(context);

        if (trackingRecords == null) {
            trackingRecords = ((TrackingRecordDAO) daoFactory.getDao(TrackingRecord.class, context))
                    .getByProjectUuid(getUuid());
        }
        return trackingRecords;
    }

    public TrackingConfiguration getTrackingConfiguration() {
        return trackingConfiguration;
    }

    public void setTrackingConfiguration(TrackingConfiguration trackingConfiguration) {
        this.trackingConfiguration = trackingConfiguration;
    }

    public TrackingConfiguration getTrackingConfiguration(Context context) {
        Preconditions.checkNotNull(context);

        if (trackingConfiguration == null) {
            trackingConfiguration = ((TrackingConfigurationDAO) daoFactory.getDao(TrackingConfiguration.class, context))
                    .getByProjectUuid(getUuid()).orNull();
        }
        return trackingConfiguration;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uuid", getUuid())
                .add("title", getTitle())
                .add("description", getDescription().orNull())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Project) && ((Project) o).getUuid().equals(getUuid());
    }

    @Override
    public int compareTo(Project another) {
        return getTitle().compareTo(another.getTitle());
    }
}
