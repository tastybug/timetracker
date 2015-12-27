package com.tastybug.timetracker.gui.projectdetail;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.Project;

public class ProjectDetailFragment extends Fragment {

    private TextView someTextView;

    public void showProjectDetailsFor(Project project) {
        someTextView.setText(project.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_project_detail, container);

        someTextView = (TextView) rootview.findViewById(R.id.someTextview);

        return rootview;
    }

}
