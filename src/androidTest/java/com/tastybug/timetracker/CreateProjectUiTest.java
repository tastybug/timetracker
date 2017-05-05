package com.tastybug.timetracker;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.tastybug.timetracker.core.ui.dashboard.ProjectsActivity;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateProjectUiTest {

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            ProjectsActivity.class);

    @After
    public void deleteProjectAfter() {
        onView(withContentDescription("Weitere Optionen")).perform(click()); // overflow
        onView(withText(R.string.menu_delete_project)).perform(click()); // menu
        onView(withText(R.string.button_delete_project)).perform(click()); // button in deletion dialog
    }

    @Test
    public void can_create_project_with_title(){
        onView(withId(R.id.menu_item_create_project)).perform(click());

        onView(withId(R.id.project_title)).perform(typeText("Ein Projekttitel"));
        onView(withText(R.string.common_create)).perform(click());

        //check(matches(withText("Hello, World!")));
    }
}
