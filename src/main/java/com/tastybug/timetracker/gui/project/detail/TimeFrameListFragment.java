package com.tastybug.timetracker.gui.project.detail;

import android.app.ListFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.TimeFrame;

public class TimeFrameListFragment extends ListFragment {

    private static final String PROJECT_UUID = "PROJECT_UUID";

    private String projectUuid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(savedInstanceState != null) {
            projectUuid = savedInstanceState.getString(PROJECT_UUID);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(TextUtils.isEmpty(projectUuid)) {
            showNoProject();
        } else {
            showProject(projectUuid);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PROJECT_UUID, projectUuid);
    }

    public void showProject(String projectUuid) {
        this.projectUuid = projectUuid;
        setListAdapter(new TimeFrameListAdapter(getActivity(), projectUuid));
    }

    public void showNoProject() {
        this.projectUuid = null;
        setListAdapter(null);
    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
        TimeFrame selectedTimeFrame = (TimeFrame) listView.getAdapter().getItem(position);
        Toast.makeText(getActivity(), "Selected " + selectedTimeFrame.getUuid(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_timeframe_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_create_custom_time_frame:
                Toast.makeText(getActivity(), "IMPL ME: Custom time frame erstellen...", Toast.LENGTH_SHORT).show();
                return true;
            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }

    public interface TimeFrameListSelectionListener {

        void onTimeFrameSelected(TimeFrame timeFrame);
    }
}
