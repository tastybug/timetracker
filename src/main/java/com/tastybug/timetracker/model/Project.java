package com.tastybug.timetracker.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.database.dao.ProjectTimeConstraintsDAO;
import com.tastybug.timetracker.database.dao.TimeFrameDAO;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.UUID;

public class Project extends Entity {

    private String uuid = UUID.randomUUID().toString();
    private String title;
    private String description;

    private ProjectTimeConstraints timeConstraints;
    private ArrayList<TimeFrame> timeFrames;


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
        Preconditions.checkNotNull(title);

        PropertyChangeEvent e = new PropertyChangeEvent(this, "title", this.title, title);
        this.title = title;
        propertyChange(e);
    }

    public Optional<String> getDescription() {
        return Optional.fromNullable(description);
    }

    public void setDescription(Optional<String> description) {
        PropertyChangeEvent e = new PropertyChangeEvent(this, "description", this.description, description);
        this.description = description.orNull();
        propertyChange(e);
    }


    public ArrayList<TimeFrame> getTimeFrames() {
        if (timeFrames == null) {
            if (!hasContext()) {
                throw new IllegalStateException("Failed to fetch timeframes lazily, no context available.");
            }
            timeFrames = ((TimeFrameDAO)daoFactory.getDao(TimeFrame.class, getContext()))
                    .getByProjectUuid(getUuid());
        }
        return timeFrames;
    }

    public TimeFrame createTimeFrame() {
        TimeFrame timeFrame = new TimeFrame();
        timeFrame.setProjectUuid(getUuid());
        timeFrame.setContext(getContext());
        propertyChange(new PropertyChangeEvent(this, "createTimeFrame", null, timeFrame));
        getTimeFrames().add(timeFrame);

        return timeFrame;
    }

    public boolean removeTimeFrame(TimeFrame timeFrame) {
        Preconditions.checkNotNull(timeFrame, "Cannot remove null time frame.");

        boolean success = getTimeFrames().remove(timeFrame);
        if(success) {
            PropertyChangeEvent e = new PropertyChangeEvent(this, "removeTimeFrame", timeFrame, null);
            propertyChange(e);
        }
        return success;
    }

    public ProjectTimeConstraints getTimeConstraints() {
        if (timeConstraints == null) {
            if(!hasContext()) {
                throw new IllegalStateException("Failed to fetch time constraints lazily, " +
                        "no context available!");
            }
            timeConstraints = ((ProjectTimeConstraintsDAO)daoFactory.getDao(ProjectTimeConstraints.class, getContext()))
                    .getByProjectUuid(getUuid());
        }
        return timeConstraints;
    }

    public String toString() {
        if (hasContext()) {
            return MoreObjects.toStringHelper(this)
                    .add("uuid", getUuid())
                    .add("title", getTitle())
                    .add("description", getDescription().orNull())
                    .add("timeConstraints", getTimeConstraints())
                    .add("timeFrames", getTimeFrames())
                    .toString();
        } else {
            return MoreObjects.toStringHelper(this)
                    .add("uuid", getUuid())
                    .add("title", getTitle())
                    .add("description", getDescription().orNull())
                    .add("timeConstraints", "skipped, no context")
                    .add("timeFrames", "skipped, no context")
                    .toString();
        }
    }
}
