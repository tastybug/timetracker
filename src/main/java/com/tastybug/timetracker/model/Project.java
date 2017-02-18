package com.tastybug.timetracker.model;

import android.support.annotation.NonNull;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Project extends Entity implements Comparable<Project> {

    private String uuid = UUID.randomUUID().toString();
    private String title;
    private String description;
    private boolean closed = false;
    private TrackingConfiguration trackingConfiguration;
    private ArrayList<TrackingRecord> trackingRecords;


    public Project(String uuid, String title, Optional<String> description, boolean closed) {
        this.uuid = uuid;
        this.title = title;
        this.description = description.orNull();
        this.closed = closed;
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
        return Optional.fromNullable(description);
    }

    public void setDescription(Optional<String> descriptionOptional) {
        Preconditions.checkNotNull(descriptionOptional);
        this.description = descriptionOptional.orNull();
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public ArrayList<TrackingRecord> getTrackingRecords() {
        return trackingRecords;
    }

    public void setTrackingRecords(ArrayList<TrackingRecord> trackingRecords) {
        this.trackingRecords = trackingRecords;
    }

    public TrackingConfiguration getTrackingConfiguration() {
        return trackingConfiguration;
    }

    public void setTrackingConfiguration(TrackingConfiguration trackingConfiguration) {
        this.trackingConfiguration = trackingConfiguration;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uuid", getUuid())
                .add("title", getTitle())
                .add("description", getDescription().orNull())
                .add("closed", isClosed())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof Project) {
            Project other = (Project) o;
            return Objects.equals(getUuid(), other.getUuid())
                    && Objects.equals(title, other.getTitle())
                    && Objects.equals(description, other.getDescription().orNull())
                    && Objects.equals(closed, other.isClosed());
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull Project another) {
        return getTitle().compareTo(another.getTitle());
    }
}
