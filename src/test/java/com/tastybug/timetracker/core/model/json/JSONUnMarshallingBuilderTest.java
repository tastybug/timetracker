package com.tastybug.timetracker.core.model.json;

import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.rounding.Rounding;

import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class JSONUnMarshallingBuilderTest {

    @Test
    public void build_can_unmarshall_an_array_of_one_project_including_configuration_and_records() throws Exception {
        // given
        Project project = aProjectWith2RecordsAndAConfiguration();
        ProjectJSON projectJSON = new ProjectJSON(project);
        JSONArray array = new JSONArray(Collections.singletonList(projectJSON));

        // when
        List<Project> projects = new JSONUnMarshallingBuilder().withProjectArray(array).build();

        // then
        assertEquals(1, projects.size());

        // and
        assertEquals(project.getUuid(), projects.get(0).getUuid());

        // and
        assertNotNull(project.getTrackingConfiguration());

        // and
        assertEquals(2, project.getTrackingRecords().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void building_with_unset_json_array_argument_yields_IAE() throws Exception {
        // when
        new JSONUnMarshallingBuilder().build();
    }

    private Project aProjectWith2RecordsAndAConfiguration() {
        Project project = new Project("uuid", "title", Optional.<String>absent(), Optional.<String>absent(), false);
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("uuid", Rounding.Strategy.FULL_MINUTE_UP);
        ArrayList<TrackingRecord> trackingRecordArrayList = new ArrayList<>();
        trackingRecordArrayList.add(new TrackingRecord("uuid"));
        trackingRecordArrayList.add(new TrackingRecord("uuid"));
        project.setTrackingConfiguration(trackingConfiguration);
        project.setTrackingRecords(trackingRecordArrayList);

        return project;
    }

}