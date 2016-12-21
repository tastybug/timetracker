package com.tastybug.timetracker.model;

import com.google.common.base.Optional;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ProjectTest {

    @Test
    public void can_set_and_remove_description() {
        // given
        Project project = new Project("project title");

        // when
        project.setDescription(Optional.of("bla"));

        // then
        assertEquals("bla", project.getDescription().orNull());

        // when
        project.setDescription(Optional.<String>absent());

        // then
        assertFalse(project.getDescription().isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void setting_null_UUID_yields_NPE() {
        // given
        Project project = new Project("project title");

        // when
        project.setUuid(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setting_null_title_yields_IAE() {
        // given
        Project project = new Project("project title");

        // when
        project.setTitle(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setting_empty_title_yields_IAE() {
        // given
        Project project = new Project("project title");

        // when
        project.setTitle("");
    }

    @Test
    public void can_serialize() {
        // given
        Project project = new Project("1234", "some title", Optional.of("a desc"));

        // when: this is supposed to cause no exception
        SerializationUtils.serialize(project);
    }
}