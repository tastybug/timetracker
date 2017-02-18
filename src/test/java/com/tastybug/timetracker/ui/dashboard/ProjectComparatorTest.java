package com.tastybug.timetracker.ui.dashboard;

import com.tastybug.timetracker.model.Project;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectComparatorTest {

    private Project projectA = mock(Project.class);
    private Project projectB = mock(Project.class);
    private Project projectC = mock(Project.class);
    private List<Project> list;

    private ProjectComparator projectComparator = new ProjectComparator();

    @Before
    public void setup() {
        when(projectA.getTitle()).thenReturn("AAA");
        when(projectB.getTitle()).thenReturn("BBB");
        when(projectC.getTitle()).thenReturn("CCC");
        list = Arrays.asList(projectA, projectB, projectC);
    }

    @Test
    public void handles_sorts_by_title_correctly() {
        when(projectA.isClosed()).thenReturn(false);
        when(projectB.isClosed()).thenReturn(false);
        when(projectC.isClosed()).thenReturn(false);

        Collections.sort(list, projectComparator);

        assertEquals(projectA, list.get(0));
        assertEquals(projectB, list.get(1));
        assertEquals(projectC, list.get(2));
    }

    @Test
    public void handles_closed_state_over_title_order() {
        when(projectA.isClosed()).thenReturn(true);
        when(projectB.isClosed()).thenReturn(false);
        when(projectC.isClosed()).thenReturn(false);

        Collections.sort(list, projectComparator);

        assertEquals(projectB, list.get(0));
        assertEquals(projectC, list.get(1));
        assertEquals(projectA, list.get(2));
    }

    @Test
    public void handles_title_comparison_between_closed_projects_by_title() {
        when(projectA.isClosed()).thenReturn(true);
        when(projectB.isClosed()).thenReturn(true);
        when(projectC.isClosed()).thenReturn(false);

        Collections.sort(list, projectComparator);

        assertEquals(projectC, list.get(0));
        assertEquals(projectA, list.get(1));
        assertEquals(projectB, list.get(2));
    }
}