package com.tastybug.timetracker.task.project;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = "target/filtered-manifest/AndroidManifest.xml")
public class CreateProjectTaskTest {

    Context context = mock(Context.class);
    ContentResolver resolver = mock(ContentResolver.class);

    @Before
    public void setup() {
        when(context.getContentResolver()).thenReturn(resolver);
    }

    @Test
    public void happyPath() throws Exception {
        // given
        CreateProjectTask task = CreateProjectTask.aTask(context).withProjectTitle("a title").withProjectDescription("a desc");

        // when
        task.execute();

        // then
        verify(resolver, times(1)).applyBatch(any(String.class), any(ArrayList.class));
    }

    @Test(expected = NullPointerException.class)
    public void lackOfProjectTitleFailsTheTask() {
        // expect
        CreateProjectTask.aTask(context).execute();
    }

}