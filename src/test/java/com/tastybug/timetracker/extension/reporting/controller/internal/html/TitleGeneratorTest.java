package com.tastybug.timetracker.extension.reporting.controller.internal.html;

import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.infrastructure.util.DefaultLocaleDateFormatter;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class TitleGeneratorTest {

    private Context context = mock(Context.class);
    private DefaultLocaleDateFormatter defaultLocaleDateFormatter = mock(DefaultLocaleDateFormatter.class);

    private TitleGenerator titleGenerator = new TitleGenerator(context, defaultLocaleDateFormatter);

    @Before
    public void setup() {
        //
    }

    @Test(expected = NullPointerException.class)
    public void getTitle_throws_NPE_on_null_project() {
        // expect
        titleGenerator.getTitle(null, new Date(), new Date());
    }

    @Test(expected = NullPointerException.class)
    public void getTitle_throws_NPE_on_null_firstDay() {
        // expect
        titleGenerator.getTitle(new Project(""), null, new Date());
    }

    @Test(expected = NullPointerException.class)
    public void getTitle_throws_NPE_on_null_lastDay() {
        // expect
        titleGenerator.getTitle(new Project(""), new Date(), null);
    }

    @Test
    public void getTitle_returns_proper_title() {
        // given
        Project project = new Project("uuid", "projectTitle", Optional.<String>absent(), Optional.<String>absent(), false);
        Date firstDay = new DateTime(2016, 12, 24, 0, 0).toDate();
        Date lastDay = new DateTime(2016, 12, 26, 0, 0).toDate();
        when(defaultLocaleDateFormatter.dateFormat(firstDay)).thenReturn("24.12.2016");
        when(defaultLocaleDateFormatter.dateFormat(lastDay)).thenReturn("26.12.2016");

        // when
        titleGenerator.getTitle(project, firstDay, lastDay);

        // then
        verify(context)
                .getString(R.string.report_title_for_project_X_from_Y_to_Z,
                        "projectTitle",
                        "24.12.2016",
                        "26.12.2016");
    }
}