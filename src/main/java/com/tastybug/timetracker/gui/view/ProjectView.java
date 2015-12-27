package com.tastybug.timetracker.gui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.Project;

public class ProjectView extends LinearLayout {

    private TextView projectTitleView, projectUuidView;


    public ProjectView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_project, this, true);

        projectTitleView = (TextView) findViewById(R.id.project_title);
        projectUuidView = (TextView) findViewById(R.id.project_uuid);
    }

    public void showProject(Project project) {
        projectTitleView.setText(project.getTitle());
        projectUuidView.setText(project.toString());
    }
}
