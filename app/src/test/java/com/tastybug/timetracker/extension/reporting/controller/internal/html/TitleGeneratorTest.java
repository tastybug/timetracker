package com.tastybug.timetracker.extension.reporting.controller.internal.html;

import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.infrastructure.util.DefaultLocaleDateFormatter;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class TitleGeneratorTest {

    private Context context = mock(Context.class);
    private DefaultLocaleDateFormatter defaultLocaleDateFormatter = mock(DefaultLocaleDateFormatter.class);

    private TitleGenerator titleGenerator = new TitleGenerator(context, defaultLocaleDateFormatter);

    @Test
    public void getTitle_returns_title_with_start_date_and_last_day_as_inclusive_day() {
        // given
        Project project = new Project("uuid", "projectTitle", Optional.<String>absent(), Optional.<String>absent(), false);
        Date notBefore = new DateTime(2016, 12, 24, 0, 0).toDate();
        Date lastDayInclusive = new DateTime(2016, 12, 25, 0, 0).toDate();
        Date notAfter = new DateTime(2016, 12, 26, 0, 0).toDate();
        given(defaultLocaleDateFormatter.dateFormat(notBefore)).willReturn("24.12.2016");
        given(defaultLocaleDateFormatter.dateFormat(lastDayInclusive)).willReturn("25.12.2016");

        // when
        titleGenerator.getTitle(project, notBefore, notAfter);

        // then
        verify(context)
                .getString(R.string.report_title_for_project_X_from_Y_to_Z,
                        "projectTitle",
                        "24.12.2016",
                        "25.12.2016");
    }
}