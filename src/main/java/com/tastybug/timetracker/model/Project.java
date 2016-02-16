package com.tastybug.timetracker.model;

import android.text.TextUtils;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.database.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;

import java.util.ArrayList;
import java.util.UUID;

public class Project extends Entity {

    private String uuid = UUID.randomUUID().toString();
    private String title;
    private String description;

    private TrackingConfiguration trackingConfiguration;
    private ArrayList<TrackingRecord> trackingRecords;


    public Project(String uuid, String title, String description) {
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
        Preconditions.checkArgument(!TextUtils.isEmpty(title), "Project has empty or null title!");
        this.title = title;
    }

    public Optional<String> getDescription() {
        return Optional.fromNullable(description);
    }

    public void setDescription(Optional<String> description) {
        Preconditions.checkNotNull(description);
        this.description = description.orNull();
    }

    public ArrayList<TrackingRecord> getTrackingRecords() {
        if (trackingRecords == null) {
            if (!hasContext()) {
                throw new IllegalStateException("Failed to fetch tracking records lazily, no context available.");
            }
            trackingRecords = ((TrackingRecordDAO)daoFactory.getDao(TrackingRecord.class, getContext()))
                    .getByProjectUuid(getUuid());
        }
        return trackingRecords;
    }

    public TrackingConfiguration getTrackingConfiguration() {
        if (trackingConfiguration == null) {
            if(!hasContext()) {
                throw new IllegalStateException("Failed to fetch tracking configuration lazily, " +
                        "no context available!");
            }
            trackingConfiguration = ((TrackingConfigurationDAO)daoFactory.getDao(TrackingConfiguration.class, getContext()))
                    .getByProjectUuid(getUuid()).orNull();
        }
        return trackingConfiguration;
    }

    public String toString() {
        if (hasContext()) {
            return MoreObjects.toStringHelper(this)
                    .add("uuid", getUuid())
                    .add("title", getTitle())
                    .add("description", getDescription().orNull())
                    .add("trackingConfiguration", getTrackingConfiguration())
                    .add("trackingRecords", getTrackingRecords())
                    .toString();
        } else {
            return MoreObjects.toStringHelper(this)
                    .add("uuid", getUuid())
                    .add("title", getTitle())
                    .add("description", getDescription().orNull())
                    .add("trackingConfiguration", "skipped, no context")
                    .add("trackingRecords", "skipped, no context")
                    .toString();
        }
    }
}
