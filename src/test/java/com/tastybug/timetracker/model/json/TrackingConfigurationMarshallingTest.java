package com.tastybug.timetracker.model.json;

import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.rounding.RoundingFactory;
import com.tastybug.timetracker.util.Formatter;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class TrackingConfigurationMarshallingTest {

    TrackingConfigurationDAO trackingConfigurationDAOMock = mock(TrackingConfigurationDAO.class);

    TrackingConfigurationMarshalling subject = new TrackingConfigurationMarshalling(trackingConfigurationDAOMock);

    @Test
    public void can_marshal_a_uuid() throws Exception {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");

        // when
        JSONObject json = subject.getAsJson(trackingConfiguration);

        // then
        assertEquals(json.getString(TrackingConfigurationMarshalling.UUID_COLUMN), trackingConfiguration.getUuid());
    }

    @Test
    public void can_marshal_a_project_uuid() throws Exception {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");

        // when
        JSONObject json = subject.getAsJson(trackingConfiguration);

        // then
        assertEquals(json.getString(TrackingConfigurationMarshalling.PROJECT_UUID_COLUMN), trackingConfiguration.getProjectUuid());
    }

    @Test
    public void can_marshal_a_hour_limit() throws Exception {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");
        trackingConfiguration.setHourLimit(Optional.of(1));

        // when
        JSONObject json = subject.getAsJson(trackingConfiguration);

        // then
        assertEquals(json.getInt(TrackingConfigurationMarshalling.HOUR_LIMIT_COLUMN), 1);
    }

    @Test
    public void can_marshal_without_hour_limit() throws Exception {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");
        trackingConfiguration.setHourLimit(Optional.<Integer>absent());

        // when
        JSONObject json = subject.getAsJson(trackingConfiguration);

        // then
        assertTrue(json.isNull(TrackingConfigurationMarshalling.HOUR_LIMIT_COLUMN));
    }

    @Test
    public void can_marshal_a_start_date() throws Exception {
        // given
        Date date = new Date();
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");
        trackingConfiguration.setStart(Optional.of(date));

        // when
        JSONObject json = subject.getAsJson(trackingConfiguration);

        // then
        assertEquals(date, Formatter.iso8601().parse(json.getString(TrackingConfigurationMarshalling.START_DATE_COLUMN)));
    }

    @Test
    public void can_marshal_without_start_date() throws Exception {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");
        trackingConfiguration.setStart(Optional.<Date>absent());

        // when
        JSONObject json = subject.getAsJson(trackingConfiguration);

        // then
        assertTrue(json.isNull(TrackingConfigurationMarshalling.START_DATE_COLUMN));
    }

    @Test
    public void can_marshal_an_end_date() throws Exception {
        // given
        Date date = new Date();
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");
        trackingConfiguration.setEnd(Optional.of(date));

        // when
        JSONObject json = subject.getAsJson(trackingConfiguration);

        // then
        assertEquals(date, Formatter.iso8601().parse(json.getString(TrackingConfigurationMarshalling.END_DATE_COLUMN)));
    }

    @Test
    public void can_marshal_without_end_date() throws Exception {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");
        trackingConfiguration.setEnd(Optional.<Date>absent());

        // when
        JSONObject json = subject.getAsJson(trackingConfiguration);

        // then
        assertTrue(json.isNull(TrackingConfigurationMarshalling.END_DATE_COLUMN));
    }

    @Test
    public void can_marshal_description_prompting_flag() throws Exception {
        // given
        Date date = new Date();
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");
        trackingConfiguration.setPromptForDescription(true);

        // when
        JSONObject json = subject.getAsJson(trackingConfiguration);

        // then
        assertEquals(true, json.getBoolean(TrackingConfigurationMarshalling.PROMPT_FOR_DESCRIPTION_COLUMN));
    }

    @Test
    public void can_marshal_all_rounding_strategies() throws Exception {
        for (RoundingFactory.Strategy strategy : RoundingFactory.Strategy.values()) {
            // given
            TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");
            trackingConfiguration.setRoundingStrategy(strategy);

            // when
            JSONObject json = subject.getAsJson(trackingConfiguration);

            // then
            assertEquals(strategy, RoundingFactory.Strategy.valueOf(json.getString(TrackingConfigurationMarshalling.ROUNDING_STRATEGY_COLUMN)));
        }
    }

    @Test
    public void can_marshall_when_no_configuration_is_available() throws Exception {
        // given
        when(trackingConfigurationDAOMock.getAll()).thenReturn(new ArrayList<TrackingConfiguration>());

        // when
        List<JSONObject> jsons = subject.generateJSON();

        // then
        assertTrue(jsons.isEmpty());

        // and
        verify(trackingConfigurationDAOMock, times(1)).getAll();
    }

    @Test
    public void can_marshall_multiple_configurations() throws Exception {
        // given
        ArrayList<TrackingConfiguration> projects = aListOfTwoConfigurations();
        when(trackingConfigurationDAOMock.getAll()).thenReturn(projects);

        // when
        List<JSONObject> jsons = subject.generateJSON();

        // then
        assertEquals(2, jsons.size());
    }

    public void can_marshall_a_specific_configuration_by_project_uuid() throws Exception {
        // given
        when(trackingConfigurationDAOMock.get(anyString())).thenReturn(Optional.of(new TrackingConfiguration("project-uuid")));

        // when
        List<JSONObject> jsons = subject.generateJSON();

        // then
        assertEquals(1, jsons.size());

        // and
        assertEquals("project-uuid", jsons.get(0).getString(TrackingConfigurationMarshalling.PROJECT_UUID_COLUMN));
    }

    ArrayList<TrackingConfiguration> aListOfTwoConfigurations() {
        ArrayList<TrackingConfiguration> list = new ArrayList<>();
        list.add(new TrackingConfiguration("one"));
        list.add(new TrackingConfiguration("two"));

        return list;
    }

}