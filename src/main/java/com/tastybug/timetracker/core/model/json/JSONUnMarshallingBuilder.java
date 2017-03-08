package com.tastybug.timetracker.core.model.json;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.core.model.Project;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class JSONUnMarshallingBuilder {

    private byte[] data;
    private JSONArray projectArray;

    public JSONUnMarshallingBuilder() {
    }

    public JSONUnMarshallingBuilder withByteArray(byte[] data) {
        this.data = data;
        return this;
    }

    public JSONUnMarshallingBuilder withProjectArray(JSONArray jsonArray) {
        this.projectArray = jsonArray;
        return this;
    }

    public List<Project> build() throws JSONException, ParseException, UnsupportedEncodingException {
        Preconditions.checkArgument(projectArray != null || data != null);

        if (data != null) {
            projectArray = new JSONArray(new String(data, "utf-8"));
        }

        ArrayList<Project> projectArrayLIst = new ArrayList<>();
        for (int i = 0; i < projectArray.length(); i++) {
            projectArrayLIst.add(new ProjectJSON(projectArray.getJSONObject(i)).toProject());
        }
        return projectArrayLIst;
    }
}
