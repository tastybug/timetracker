package com.tastybug.timetracker.model.json;

import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.rounding.RoundingFactory;
import com.tastybug.timetracker.util.Formatter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class TrackingConfigurationJSONTest {

    @Test
    public void can_marshal_a_uuid() throws Exception {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");

        // when
        TrackingConfigurationJSON json = new TrackingConfigurationJSON(trackingConfiguration);

        // then
        assertEquals(json.getString(TrackingConfigurationJSON.UUID_COLUMN), trackingConfiguration.getUuid());
    }

    @Test
    public void can_marshal_a_project_uuid() throws Exception {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");

        // when
        TrackingConfigurationJSON json = new TrackingConfigurationJSON(trackingConfiguration);

        // then
        assertEquals(trackingConfiguration.getProjectUuid(),
                json.getString(TrackingConfigurationJSON.PROJECT_UUID_COLUMN));
    }

    @Test
    public void can_marshal_a_hour_limit() throws Exception {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");
        trackingConfiguration.setHourLimit(Optional.of(1));

        // when
        TrackingConfigurationJSON json = new TrackingConfigurationJSON(trackingConfiguration);

        // then
        assertEquals(1, json.getInt(TrackingConfigurationJSON.HOUR_LIMIT_COLUMN));
    }

    @Test
    public void can_marshal_without_hour_limit() throws Exception {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");
        trackingConfiguration.setHourLimit(Optional.<Integer>absent());

        // when
        TrackingConfigurationJSON json = new TrackingConfigurationJSON(trackingConfiguration);

        // then
        assertTrue(json.isNull(TrackingConfigurationJSON.HOUR_LIMIT_COLUMN));
    }

    @Test
    public void can_marshal_a_start_date() throws Exception {
        // given
        Date date = new Date();
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");
        trackingConfiguration.setStart(Optional.of(date));

        // when
        TrackingConfigurationJSON json = new TrackingConfigurationJSON(trackingConfiguration);

        // then
        assertEquals(date, Formatter.iso8601().parse(json.getString(TrackingConfigurationJSON.START_DATE_COLUMN)));
    }

    @Test
    public void can_marshal_without_start_date() throws Exception {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");
        trackingConfiguration.setStart(Optional.<Date>absent());

        // when
        TrackingConfigurationJSON json = new TrackingConfigurationJSON(trackingConfiguration);

        // then
        assertTrue(json.isNull(TrackingConfigurationJSON.START_DATE_COLUMN));
    }

    @Test
    public void can_marshal_an_end_date() throws Exception {
        // given
        Date date = new Date();
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");
        trackingConfiguration.setEnd(Optional.of(date));

        // when
        TrackingConfigurationJSON json = new TrackingConfigurationJSON(trackingConfiguration);

        // then
        assertEquals(date, Formatter.iso8601().parse(json.getString(TrackingConfigurationJSON.END_DATE_COLUMN)));
    }

    @Test
    public void can_marshal_without_end_date() throws Exception {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");
        trackingConfiguration.setEnd(Optional.<Date>absent());

        // when
        TrackingConfigurationJSON json = new TrackingConfigurationJSON(trackingConfiguration);

        // then
        assertTrue(json.isNull(TrackingConfigurationJSON.END_DATE_COLUMN));
    }

    @Test
    public void can_marshal_description_prompting_flag() throws Exception {
        // given
        Date date = new Date();
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");
        trackingConfiguration.setPromptForDescription(true);

        // when
        TrackingConfigurationJSON json = new TrackingConfigurationJSON(trackingConfiguration);

        // then
        assertEquals(true, json.getBoolean(TrackingConfigurationJSON.PROMPT_FOR_DESCRIPTION_COLUMN));
    }

    @Test
    public void can_marshal_all_rounding_strategies() throws Exception {
        for (RoundingFactory.Strategy strategy : RoundingFactory.Strategy.values()) {
            // given
            TrackingConfiguration trackingConfiguration = new TrackingConfiguration("project-uuid");
            trackingConfiguration.setRoundingStrategy(strategy);

            // when
            TrackingConfigurationJSON json = new TrackingConfigurationJSON(trackingConfiguration);

            // then
            assertEquals(strategy, RoundingFactory.Strategy.valueOf(json.getString(TrackingConfigurationJSON.ROUNDING_STRATEGY_COLUMN)));
        }
    }

}