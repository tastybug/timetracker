package com.tastybug.timetracker.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class ProjectTest {

    @Test
    public void canCreateProjectWithTitle() {
        // when
        Project project = new Project("name");

        // then
        assertNotNull(project);
        assertEquals("name", project.getTitle());
    }

    @Test
    public void noProjectDescriptionIsHandledWell() {
        // given
        Project project = new Project("name");

        // when
        project.setDescription("desc");

        // then
        assertEquals("desc", project.getDescription().get());

        // when
        project.setDescription(null);

        // then
        assertFalse(project.getDescription().isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullProjectTitle() {
        // given
        Project project = new Project("name");

        // when
        project.setTitle(null);
    }

}