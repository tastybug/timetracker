package com.tastybug.timetracker.model.json;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.Project;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class JSONUnMarshallingBuilder {

    private JSONArray projectArray;

    public JSONUnMarshallingBuilder() {}

    public JSONUnMarshallingBuilder withProjectArray(JSONArray jsonArray) {
        this.projectArray = jsonArray;
        return this;
    }

    public List<Project> build() throws JSONException, ParseException {
        Preconditions.checkNotNull(projectArray);

        ArrayList<Project> projectArrayLIst = new ArrayList<>();
        for (int i = 0; i < projectArray.length(); i++) {
            projectArrayLIst.add(new ProjectJSON(projectArray.getJSONObject(i)).toProject());
        }
        return projectArrayLIst;
    }
}
