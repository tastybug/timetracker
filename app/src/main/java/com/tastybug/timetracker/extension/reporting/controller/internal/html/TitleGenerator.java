package com.tastybug.timetracker.extension.reporting.controller.internal.html;

import android.content.Context;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.infrastructure.util.DefaultLocaleDateFormatter;

import java.util.Date;

class TitleGenerator {

    private DefaultLocaleDateFormatter dateFormatter;
    private Context context;

    TitleGenerator(Context context) {
        this(context, new DefaultLocaleDateFormatter());
    }

    TitleGenerator(Context context,
                   DefaultLocaleDateFormatter defaultLocaleDateFormatter) {
        this.context = context;
        this.dateFormatter = defaultLocaleDateFormatter;
    }

    public String getTitle(Project project, Date firstDay, Date lastDay) {
        return context.getString(R.string.report_title_for_project_X_from_Y_to_Z,
                project.getTitle(),
                dateFormatter.dateFormat(firstDay),
                dateFormatter.dateFormat(lastDay));
    }
}
