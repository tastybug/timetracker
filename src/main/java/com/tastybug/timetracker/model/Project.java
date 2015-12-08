package com.tastybug.timetracker.model;

import android.support.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class Project {

    private String title;

    @Nullable
    private String description;

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
}
