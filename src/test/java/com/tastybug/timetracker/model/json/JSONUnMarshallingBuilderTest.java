package com.tastybug.timetracker.model.json;

import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.rounding.RoundingFactory;

import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class JSONUnMarshallingBuilderTest {

    @Test
    public void build_can_unmarshal_an_array_of_one_project() throws Exception {
        // given
        Project project = aProjectWith2RecordsAndAConfiguration();
        ProjectJSON projectJSON = new ProjectJSON(project);
        JSONArray array = new JSONArray(Arrays.asList(projectJSON));

        // when
        List<Project> projects = new JSONUnMarshallingBuilder().withProjectArray(array).build();

        // then
        assertEquals(1, projects.size());

        // and
        assertEquals(project.getUuid(), projects.get(0).getUuid());
    }

    @Test(expected = IllegalArgumentException.class)
    public void building_with_unset_json_array_argument_yields_IAE() throws Exception {
        // when
        new JSONUnMarshallingBuilder().build();
    }

    private Project aProjectWith2RecordsAndAConfiguration() {
        Project project = new Project("uuid", "title", Optional.<String>absent());
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("uuid", RoundingFactory.Strategy.NO_ROUNDING);
        ArrayList<TrackingRecord> trackingRecordArrayList = new ArrayList<>();
        trackingRecordArrayList.add(new TrackingRecord("uuid"));
        trackingRecordArrayList.add(new TrackingRecord("uuid"));
        project.setTrackingConfiguration(trackingConfiguration);
        project.setTrackingRecords(trackingRecordArrayList);

        return project;
    }

}