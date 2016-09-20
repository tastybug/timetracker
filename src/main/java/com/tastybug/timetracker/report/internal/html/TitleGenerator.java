package com.tastybug.timetracker.report.internal.html;

import android.content.Context;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.util.DefaultLocaleDateFormatter;

import java.util.Date;

public class TitleGenerator {

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
        Preconditions.checkNotNull(project);
        Preconditions.checkNotNull(firstDay);
        Preconditions.checkNotNull(lastDay);

        return context.getString(R.string.report_title_for_project_X_from_Y_to_Z,
                project.getTitle(),
                dateFormatter.dateFormat(firstDay),
                dateFormatter.dateFormat(lastDay));
    }
}
