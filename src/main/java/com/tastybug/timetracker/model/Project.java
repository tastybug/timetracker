package com.tastybug.timetracker.model;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.util.database.EntityDAO;
import com.tastybug.timetracker.util.database.ProjectDAO;

import java.beans.PropertyChangeEvent;

public class Project extends Entity {

    private Integer id;

    private String title;

    @Nullable
    private String description;


    public Project(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public Project(String title) {
        this.title = title;
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

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    protected EntityDAO getDefaultDAOInstance(Context context) {
        return new ProjectDAO(context);
    }
}
