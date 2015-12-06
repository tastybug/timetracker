package com.tastybug.timetracker.model;

import android.support.annotation.Nullable;

import java.util.ArrayList;

public class Project {

    private String title;

    @Nullable
    private String description;

    private ArrayList<TimeFrame> timeFrames = new ArrayList<TimeFrame>();

    public Project(String title) {
        this.title = title;
    }



}
