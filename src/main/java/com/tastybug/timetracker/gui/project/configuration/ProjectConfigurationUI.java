package com.tastybug.timetracker.gui.project.configuration;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;

public class ProjectConfigurationUI {

    private EditText projectTitleEditText;
    private EditText projectDescriptionEditText;

    private Context context;

    public ProjectConfigurationUI(Context context) {
        this.context = context;
    }

    public View inflateWidgets(LayoutInflater inflater,
                               ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_project_configuration, container);

        projectTitleEditText = (EditText) view.findViewById(R.id.project_title);
        projectDescriptionEditText = (EditText) view.findViewById(R.id.project_description);

        return view;
    }

    public void showProjectData(String title, Optional<String> description) {
        projectTitleEditText.setText(title);
        projectDescriptionEditText.setText(description.isPresent() ? description.get() : "");
    }

    public Optional<String> getTitleFromWidget(boolean blame) {
        Optional<String> title = TextUtils.isEmpty(projectTitleEditText.getText())
                ? Optional.<String>absent()
                : Optional.of(projectDescriptionEditText.getText().toString());
        if (blame) {
            projectTitleEditText.setError(title.isPresent()
                    ? null
                    : context.getString(R.string.error_project_title_empty));
        }
        return title;
    }

    public Optional<String> getDescriptionFromWidget() {
        return TextUtils.isEmpty(projectDescriptionEditText.getText())
                ? Optional.<String>absent()
                : Optional.of(projectDescriptionEditText.getText().toString());
    }

}
