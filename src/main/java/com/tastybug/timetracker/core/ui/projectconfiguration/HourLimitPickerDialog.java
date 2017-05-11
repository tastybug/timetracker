package com.tastybug.timetracker.core.ui.projectconfiguration;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class HourLimitPickerDialog extends DialogPreference {

    private EditText numberEditText;
    private Integer initialValue;

    public HourLimitPickerDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        numberEditText = new EditText(getContext());
        numberEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        return numberEditText;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        if (which == DialogInterface.BUTTON_POSITIVE) {
            this.initialValue = getNewHourLimit();
            persistInt(initialValue);
            callChangeListener(initialValue);
        }
    }

    private int getNewHourLimit() {
        Editable text = numberEditText.getText();
        if (TextUtils.isEmpty(text)) {
            return 0;
        } else {
            return Integer.parseInt(text.toString());
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue,
                                     Object defaultValue) {
        int def = (defaultValue instanceof Number) ? (Integer) defaultValue
                : (defaultValue != null) ? Integer.parseInt(defaultValue.toString()) : 1;
        if (restorePersistedValue) {
            this.initialValue = getPersistedInt(def);
        } else {
            this.initialValue = (Integer) defaultValue;
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 1);
    }
}