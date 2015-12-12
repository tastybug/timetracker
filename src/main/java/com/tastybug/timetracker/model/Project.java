package com.tastybug.timetracker.model;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.util.database.EntityDAO;
import com.tastybug.timetracker.util.database.ProjectDAO;

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
        this.title = title;
    }

    public Optional<String> getDescription() {
        return Optional.fromNullable(description);
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Override
    public ContentValues toContentValues() {
        return null;
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
