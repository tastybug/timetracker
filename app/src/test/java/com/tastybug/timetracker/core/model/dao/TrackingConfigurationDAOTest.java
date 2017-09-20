package com.tastybug.timetracker.core.model.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.rounding.Rounding;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static com.tastybug.timetracker.core.model.dao.TrackingConfigurationDAO.END_DATE_COLUMN;
import static com.tastybug.timetracker.core.model.dao.TrackingConfigurationDAO.HOUR_LIMIT_COLUMN;
import static com.tastybug.timetracker.core.model.dao.TrackingConfigurationDAO.PROJECT_UUID_COLUMN;
import static com.tastybug.timetracker.core.model.dao.TrackingConfigurationDAO.PROMPT_FOR_DESCRIPTION_COLUMN;
import static com.tastybug.timetracker.core.model.dao.TrackingConfigurationDAO.ROUNDING_STRATEGY_COLUMN;
import static com.tastybug.timetracker.core.model.dao.TrackingConfigurationDAO.START_DATE_COLUMN;
import static com.tastybug.timetracker.core.model.dao.TrackingConfigurationDAO.UUID_COLUMN;
import static com.tastybug.timetracker.core.model.dao.TrackingRecordDAO.ID_COLUMN;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.guava.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class TrackingConfigurationDAOTest {

    private Context context = mock(Context.class);
    private ContentResolver resolver = mock(ContentResolver.class);

    // test subject
    private TrackingConfigurationDAO trackingConfigurationDAO = new TrackingConfigurationDAO(context);

    private static SimpleDateFormat getIso8601DateFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
    }

    @Before
    public void setup() {
        given(context.getContentResolver()).willReturn(resolver);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void canGetExistingEntityById() {
        // given
        Cursor cursor = aProjectTimeConstraintsCursor("1");
        given(resolver.query(any(Uri.class), any(String[].class), any(String.class), any(String[].class), (String) isNull()))
                .willReturn(cursor);

        // when
        Optional<TrackingConfiguration> trackingConfiguration = trackingConfigurationDAO.get("1");

        // then
        assertThat(trackingConfiguration).isPresent();
        assertThat(trackingConfiguration.get().getUuid()).isEqualTo("1");
        assertThat(trackingConfiguration.get().getProjectUuid()).isEqualTo("2");
        assertThat(trackingConfiguration.get().getHourLimit()).extractingValue().isEqualTo(5);
        assertThat(trackingConfiguration.get().getStart()).isNotNull();
        assertThat(trackingConfiguration.get().getEnd()).isNotNull();
    }

    @Test
    public void gettingNonExistingEntityByIdYieldsNull() {
        // given
        Cursor cursor = anEmptyCursor();
        given(resolver.query(any(Uri.class), any(String[].class), any(String.class), any(String[].class), (String) isNull()))
                .willReturn(cursor);

        // when
        Optional<TrackingConfiguration> trackingConfiguration = trackingConfigurationDAO.get("1");

        // then
        assertThat(trackingConfiguration).isAbsent();
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedStartDateStringLeadsToException() {
        // given
        Cursor cursor = aProjectTimeConstraintsCursor("1", "abc", getIso8601DateFormatter().format(new LocalDate().toDate()));
        given(resolver.query(any(Uri.class), any(String[].class), any(String.class), any(String[].class), (String) isNull()))
                .willReturn(cursor);

        // when
        trackingConfigurationDAO.get("1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedEndDateStringLeadsToException() {
        // given
        Cursor cursor = aProjectTimeConstraintsCursor("1", getIso8601DateFormatter().format(new LocalDate().toDate()), "abc");
        given(resolver.query(any(Uri.class), any(String[].class), any(String.class), any(String[].class), (String) isNull()))
                .willReturn(cursor);

        // when
        trackingConfigurationDAO.get("1");
    }

    @Test
    public void getAllWorksForExistingTrackingRecords() {
        // given
        Cursor aCursorWith2TrackingRecords = aCursorWith2Entities();
        given(resolver.query(any(Uri.class), any(String[].class), (String) isNull(), (String[]) isNull(), (String) isNull()))
                .willReturn(aCursorWith2TrackingRecords);

        // when
        List<TrackingConfiguration> entities = trackingConfigurationDAO.getAll();

        // then
        assertThat(entities.size()).isEqualTo(2);
    }

    @Test
    public void getAllReturnsEmptyListForLackOfEntities() {
        // given
        Cursor noProjects = anEmptyCursor();
        given(resolver.query(any(Uri.class), any(String[].class), (String) isNull(), (String[]) isNull(), (String) isNull()))
                .willReturn(noProjects);

        // when
        List<TrackingConfiguration> entities = trackingConfigurationDAO.getAll();

        // then
        assertThat(entities).isEmpty();
    }

    @Test
    public void canCreateEntity() {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("some project-uuid");

        // when
        trackingConfigurationDAO.create(trackingConfiguration);

        // then
        assertThat(trackingConfiguration.getUuid()).isNotNull();
    }

    @Test
    public void canUpdateEntity() {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("some project-uuid");
        given(resolver.update(any(Uri.class), any(ContentValues.class), any(String.class), any(String[].class))).willReturn(1);

        // when
        int updateCount = trackingConfigurationDAO.update(trackingConfiguration);

        // then
        assertThat(updateCount).isEqualTo(1);
    }

    @Test
    public void canDeleteEntity() {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("some project-uuid");
        given(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).willReturn(1);

        // when
        boolean success = trackingConfigurationDAO.delete(trackingConfiguration);

        // then
        assertThat(success).isTrue();
    }

    @Test
    public void deleteReturnsFalseWhenNotSuccessful() {
        // given
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("some project-uuid");
        given(resolver.delete(any(Uri.class), any(String.class), any(String[].class))).willReturn(0);

        // when
        boolean success = trackingConfigurationDAO.delete(trackingConfiguration);

        // then
        assertThat(success).isFalse();
    }

    @Test
    public void providesCorrectPrimaryKeyColumn() {
        // expect
        assertThat(trackingConfigurationDAO.getPKColumn()).isEqualTo(ID_COLUMN);
    }

    @Test
    public void knowsAllColumns() {
        // expect
        assertThat(trackingConfigurationDAO.getColumns())
                .containsOnly(
                        UUID_COLUMN,
                        PROJECT_UUID_COLUMN,
                        HOUR_LIMIT_COLUMN,
                        START_DATE_COLUMN,
                        END_DATE_COLUMN,
                        PROMPT_FOR_DESCRIPTION_COLUMN,
                        ROUNDING_STRATEGY_COLUMN);
    }

    @Test(expected = NullPointerException.class)
    public void gettingTrackingConfigurationByNullProjectUuidYieldsException() {
        // expect
        trackingConfigurationDAO.getByProjectUuid(null);
    }

    @Test
    public void gettingTrackingConfigurationByUnknownProjectUuidYieldsNull() {
        // given
        Cursor cursor = anEmptyCursor();
        given(resolver.query(any(Uri.class), any(String[].class), any(String.class), any(String[].class), (String) isNull()))
                .willReturn(cursor);

        // when
        Optional<TrackingConfiguration> trackingConfigurationOpt = trackingConfigurationDAO.getByProjectUuid("1");

        // then
        assertThat(trackingConfigurationOpt).isAbsent();
    }

    @Test
    public void canGetTrackingConfigurationByProjectUuid() {
        // given
        Cursor cursor = aProjectTimeConstraintsCursor("111");
        given(resolver.query(any(Uri.class), any(String[].class), any(String.class), any(String[].class), (String) isNull()))
                .willReturn(cursor);

        // when
        Optional<TrackingConfiguration> trackingConfigurationOpt = trackingConfigurationDAO.getByProjectUuid("1");

        // then
        assertThat(trackingConfigurationOpt).isPresent();
    }

    private Cursor aProjectTimeConstraintsCursor(String uuid) {
        Cursor cursor = mock(Cursor.class);
        given(cursor.getString(0)).willReturn(uuid);
        given(cursor.getString(1)).willReturn("2");
        given(cursor.getInt(2)).willReturn(5);
        given(cursor.getString(3)).willReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        given(cursor.getString(4)).willReturn(getIso8601DateFormatter().format(new LocalDate().plusDays(1).toDate()));
        given(cursor.getInt(5)).willReturn(1);
        given(cursor.getString(6)).willReturn(Rounding.Strategy.NO_ROUNDING.name());
        given(cursor.moveToFirst()).willReturn(true);

        return cursor;
    }

    private Cursor aProjectTimeConstraintsCursor(String uuid, String startDateString, String endDateString) {
        Cursor cursor = mock(Cursor.class);
        given(cursor.getString(0)).willReturn(uuid);
        given(cursor.getString(1)).willReturn("2");
        given(cursor.getInt(2)).willReturn(5);
        given(cursor.getString(3)).willReturn(startDateString);
        given(cursor.getString(4)).willReturn(endDateString);
        given(cursor.getInt(5)).willReturn(1);
        given(cursor.getString(6)).willReturn(Rounding.Strategy.NO_ROUNDING.name());
        given(cursor.moveToFirst()).willReturn(true);

        return cursor;
    }

    private Cursor aCursorWith2Entities() {
        Cursor cursor = mock(Cursor.class);
        given(cursor.getString(0)).willReturn("1");
        given(cursor.getString(1)).willReturn("2");
        given(cursor.getInt(2)).willReturn(5);
        given(cursor.getString(3)).willReturn(getIso8601DateFormatter().format(new LocalDate().toDate()));
        given(cursor.getString(4)).willReturn(getIso8601DateFormatter().format(new LocalDate().plusDays(1).toDate()));
        given(cursor.getInt(5)).willReturn(1);
        given(cursor.getString(6)).willReturn(Rounding.Strategy.NO_ROUNDING.name());
        given(cursor.moveToNext()).willReturn(true, true, false);

        return cursor;
    }

    private Cursor anEmptyCursor() {
        Cursor cursor = mock(Cursor.class);
        given(cursor.moveToFirst()).willReturn(false);
        given(cursor.moveToNext()).willReturn(false);

        return cursor;
    }
}