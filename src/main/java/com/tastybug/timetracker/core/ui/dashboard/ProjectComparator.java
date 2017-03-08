package com.tastybug.timetracker.core.ui.dashboard;

import com.tastybug.timetracker.core.model.Project;

import java.util.Comparator;

class ProjectComparator implements Comparator<Project> {

    @Override
    public int compare(Project o1, Project o2) {
        return o1.isClosed() || o2.isClosed() ? compareClosed(o1, o2) : compareTitle(o1, o2);
    }

    private int compareClosed(Project o1, Project o2) {
        if (o1.isClosed()) {
            if (o2.isClosed()) {
                return compareTitle(o1, o2);
            } else {
                return 1;
            }
        } else {
            return -1;
        }
    }

    private int compareTitle(Project o1, Project o2) {
        return o1.getTitle().compareTo(o2.getTitle());
    }
}
