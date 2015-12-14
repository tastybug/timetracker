package com.tastybug.timetracker.model;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.util.database.EntityDAO;
import com.tastybug.timetracker.util.database.ProjectDAO;

import java.beans.PropertyChangeEvent;
import java.util.UUID;

public class Project extends Entity {

    private String uuid = UUID.randomUUID().toString();
    private String title;
    private String description;


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

    public void setDescription(@Nullable String description) {
        PropertyChangeEvent e = new PropertyChangeEvent(this, "description", this.description, description);
        this.description = description;
        propertyChange(e);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uuid", getUuid())
                .add("title", getTitle())
                .add("description", getDescription().orNull())
                .toString();
    }

    @Override
    protected EntityDAO getDefaultDAOInstance(Context context) {
        return new ProjectDAO(context);
    }
}
