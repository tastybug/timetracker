package com.tastybug.timetracker.model.json;

import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.rounding.RoundingFactory;
import com.tastybug.timetracker.util.Formatter;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static com.tastybug.timetracker.model.json.TrackingConfigurationJSON.END_DATE_COLUMN;
import static com.tastybug.timetracker.model.json.TrackingConfigurationJSON.HOUR_LIMIT_COLUMN;
import static com.tastybug.timetracker.model.json.TrackingConfigurationJSON.PROJECT_UUID_COLUMN;
import static com.tastybug.timetracker.model.json.TrackingConfigurationJSON.PROMPT_FOR_DESCRIPTION_COLUMN;
import static com.tastybug.timetracker.model.json.TrackingConfigurationJSON.ROUNDING_STRATEGY_COLUMN;
import static com.tastybug.timetracker.model.json.TrackingConfigurationJSON.START_DATE_COLUMN;
import static com.tastybug.timetracker.model.json.TrackingConfigurationJSON.UUID_COLUMN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        assertEquals(json.getString(UUID_COLUMN), trackingConfiguration.getUuid());
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

    @Test
    public void can_import_a_json() throws Exception {
        // given
        JSONObject toImportFrom = aTrackingConfigurationJSONToImport();

        // when
        TrackingConfigurationJSON subject = new TrackingConfigurationJSON(toImportFrom);

        // then
        assertEquals(toImportFrom.get(UUID_COLUMN), subject.get(UUID_COLUMN));
        assertEquals(toImportFrom.get(PROJECT_UUID_COLUMN), subject.get(PROJECT_UUID_COLUMN));
        assertEquals(toImportFrom.getInt(HOUR_LIMIT_COLUMN), subject.getInt(HOUR_LIMIT_COLUMN));
        assertEquals(toImportFrom.getString(START_DATE_COLUMN), subject.getString(START_DATE_COLUMN));
        assertEquals(toImportFrom.getString(END_DATE_COLUMN), subject.getString(END_DATE_COLUMN));
        assertEquals(toImportFrom.getBoolean(PROMPT_FOR_DESCRIPTION_COLUMN), subject.getBoolean(PROMPT_FOR_DESCRIPTION_COLUMN));
        assertEquals(toImportFrom.getString(ROUNDING_STRATEGY_COLUMN), subject.getString(ROUNDING_STRATEGY_COLUMN));
    }

    @Test
    public void can_import_a_json_without_hour_limit() throws Exception {
        // given
        JSONObject toImportFrom = aTrackingConfigurationJSONToImport();
        toImportFrom.put(HOUR_LIMIT_COLUMN, null);

        // when
        TrackingConfigurationJSON subject = new TrackingConfigurationJSON(toImportFrom);

        // then
        assertTrue(subject.isNull(HOUR_LIMIT_COLUMN));
    }

    @Test
    public void can_import_a_json_without_start_date() throws Exception {
        // given
        JSONObject toImportFrom = aTrackingConfigurationJSONToImport();
        toImportFrom.put(START_DATE_COLUMN, null);

        // when
        TrackingConfigurationJSON subject = new TrackingConfigurationJSON(toImportFrom);

        // then
        assertTrue(subject.isNull(START_DATE_COLUMN));
    }

    @Test
    public void can_import_a_json_without_end_date() throws Exception {
        // given
        JSONObject toImportFrom = aTrackingConfigurationJSONToImport();
        toImportFrom.put(END_DATE_COLUMN, null);

        // when
        TrackingConfigurationJSON subject = new TrackingConfigurationJSON(toImportFrom);

        // then
        assertTrue(subject.isNull(END_DATE_COLUMN));
    }

    @Test
    public void to_tracking_configuration_contains_all_attributes_if_set() throws Exception {
        // given
        JSONObject toImportFrom = aTrackingConfigurationJSONToImport();

        // when
        TrackingConfiguration trackingConfiguration = new TrackingConfigurationJSON(toImportFrom).toTrackingConfiguration();

        // then
        assertEquals(toImportFrom.get(UUID_COLUMN), trackingConfiguration.getUuid());
        assertEquals(toImportFrom.get(PROJECT_UUID_COLUMN), trackingConfiguration.getProjectUuid());
        assertEquals(toImportFrom.getInt(HOUR_LIMIT_COLUMN), trackingConfiguration.getHourLimit().get().intValue());
        assertEquals(toImportFrom.getString(START_DATE_COLUMN), Formatter.iso8601().format(trackingConfiguration.getStart().get()));
        assertEquals(toImportFrom.getString(END_DATE_COLUMN), Formatter.iso8601().format(trackingConfiguration.getEnd().get()));
        assertEquals(toImportFrom.getBoolean(PROMPT_FOR_DESCRIPTION_COLUMN), trackingConfiguration.isPromptForDescription());
        assertEquals(toImportFrom.getString(ROUNDING_STRATEGY_COLUMN), trackingConfiguration.getRoundingStrategy().name());
    }

    @Test
    public void to_tracking_configuration_can_deal_with_missing_hour_limits() throws Exception {
        // given
        JSONObject toImportFrom = aTrackingConfigurationJSONToImport();
        toImportFrom.put(HOUR_LIMIT_COLUMN, null);

        // when
        TrackingConfiguration trackingConfiguration = new TrackingConfigurationJSON(toImportFrom).toTrackingConfiguration();

        // then
        assertFalse(trackingConfiguration.getHourLimit().isPresent());
    }

    @Test
    public void to_tracking_configuration_can_deal_with_missing_start_dates() throws Exception {
        // given
        JSONObject toImportFrom = aTrackingConfigurationJSONToImport();
        toImportFrom.put(START_DATE_COLUMN, null);

        // when
        TrackingConfiguration trackingConfiguration = new TrackingConfigurationJSON(toImportFrom).toTrackingConfiguration();

        // then
        assertFalse(trackingConfiguration.getStart().isPresent());
    }

    @Test
    public void to_tracking_configuration_can_deal_with_missing_end_dates() throws Exception {
        // given
        JSONObject toImportFrom = aTrackingConfigurationJSONToImport();
        toImportFrom.put(END_DATE_COLUMN, null);

        // when
        TrackingConfiguration trackingConfiguration = new TrackingConfigurationJSON(toImportFrom).toTrackingConfiguration();

        // then
        assertFalse(trackingConfiguration.getEnd().isPresent());
    }

    @Test
    public void to_tracking_configuration_can_deal_with_any_type_of_rounding_strategy() throws Exception {
        for (RoundingFactory.Strategy strategy : RoundingFactory.Strategy.values()) {
            // given
            JSONObject toImportFrom = aTrackingConfigurationJSONToImport();
            toImportFrom.put(ROUNDING_STRATEGY_COLUMN, strategy.name());

            // when
            TrackingConfiguration trackingConfiguration = new TrackingConfigurationJSON(toImportFrom).toTrackingConfiguration();

            // then
            assertEquals(strategy, trackingConfiguration.getRoundingStrategy());
        }
    }

    private JSONObject aTrackingConfigurationJSONToImport() throws JSONException{
        JSONObject toImportFrom = new JSONObject();
        toImportFrom.put(UUID_COLUMN, "1234");
        toImportFrom.put(PROJECT_UUID_COLUMN, "1234");
        toImportFrom.put(HOUR_LIMIT_COLUMN, 1);
        toImportFrom.put(START_DATE_COLUMN, Formatter.iso8601().format(new Date()));
        toImportFrom.put(END_DATE_COLUMN, Formatter.iso8601().format(new Date()));
        toImportFrom.put(PROMPT_FOR_DESCRIPTION_COLUMN, false);
        toImportFrom.put(ROUNDING_STRATEGY_COLUMN, RoundingFactory.Strategy.NO_ROUNDING.name());

        return toImportFrom;
    }
}