package com.tastybug.timetracker.task.project;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;

import com.tastybug.timetracker.database.dao.ProjectDAO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = "target/filtered-manifest/AndroidManifest.xml")
public class DeleteProjectTaskTest {

    Context context = mock(Context.class);
    ContentResolver resolver = mock(ContentResolver.class);

    @Before
    public void setup() {
        when(context.getContentResolver()).thenReturn(resolver);
    }

    @Test
    public void happyPath() throws Exception {
        // given
        DeleteProjectTask task = DeleteProjectTask.aTask(context).withProjectUuid("123");

        // when
        task.execute();

        // then
        verify(resolver, times(1)).delete(eq(new ProjectDAO(context).getQueryUri()), isA(String.class), eq(new String[] {"123"}));
    }

    @Test(expected = NullPointerException.class)
    public void lackOfProjectTitleFailsTheTask() {
        // expect
        DeleteProjectTask.aTask(context).execute();
    }

}