package com.tastybug.timetracker.core.ui.projectconfiguration;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class HourLimitPickerDialog extends DialogPreference {

    EditText numberEditText;
    Integer initialValue;

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
            this.initialValue = Integer.parseInt(numberEditText.getText().toString());
            persistInt(initialValue);
            callChangeListener(initialValue);
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