package com.tastybug.timetracker.extension.wifitracking.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.extension.wifitracking.controller.OSConnectivityIndicator;
import com.tastybug.timetracker.extension.wifitracking.controller.checkin.TriggerRepository;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;

public class ManageWifiTrackingDialogFragment extends DialogFragment {

    private static final String PROJECT_UUID = "PROJECT_UUID";
    private static final String SSID = "SSID";

    private String projectUuid;
    private EditText ssidEditText;

    public ManageWifiTrackingDialogFragment() {
        new OttoProvider().getSharedBus().register(this);
    }

    public static ManageWifiTrackingDialogFragment aDialog(String projectUuid) {
        Bundle bundle = new Bundle();
        bundle.putString(PROJECT_UUID, projectUuid);

        ManageWifiTrackingDialogFragment dialogFragment = new ManageWifiTrackingDialogFragment();
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Optional<String> ssid;
        if (savedInstanceState != null) {
            projectUuid = savedInstanceState.getString(PROJECT_UUID);
            ssid = Optional.fromNullable(savedInstanceState.getString(SSID));
        } else {
            projectUuid = getArguments().getString(PROJECT_UUID);
            ssid = getCurrentSsid(projectUuid);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(prepareViews(ssid))
                .setTitle(R.string.wifi_trigger_creation_dialog_title)
                .setPositiveButton(R.string.common_create, null)
                .setNegativeButton(R.string.common_cancel, null);
        if (getCurrentSsid(projectUuid).isPresent()) {
            builder.setNeutralButton(R.string.common_discard, null);
        }
        final AlertDialog alertDialog = builder.create();
        fixDialogButtonsDontDismiss(alertDialog);
        return alertDialog;
    }

    private View prepareViews(Optional<String> preselectedSsidOptional) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_manage_wifi_trigger, null);
        ImageButton ssidPickCurrent = (ImageButton) rootView.findViewById(R.id.ssid_pick_current);
        ssidPickCurrent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Optional<WifiInfo> info = new OSConnectivityIndicator(getActivity()).getWifiConnectionInfo();
                if (info.isPresent()) {
                    Toast.makeText(getActivity(), R.string.toast_current_ssid_selected, Toast.LENGTH_SHORT).show();
                    ssidEditText.setText(info.get().getSSID());
                } else {
                    Toast.makeText(getActivity(), R.string.toast_currently_not_connected_cannot_pick_ssid, Toast.LENGTH_LONG).show();
                }
            }
        });

        ssidEditText =(EditText) rootView.findViewById(R.id.ssid);
        if (preselectedSsidOptional.isPresent()) {
            ssidEditText.setText(preselectedSsidOptional.get());
        }

        return rootView;
    }

    private Optional<String> getCurrentSsid(String projectUuid) {
        Optional<TriggerRepository.Trigger> triggerOptional = new TriggerRepository(getActivity()).getByProjectUuid(projectUuid);
        if (triggerOptional.isPresent()) {
            return Optional.of(triggerOptional.get().getSsid());
        } else {
            return Optional.absent();
        }
    }

    private Optional<String> getSelectedSsid(boolean blame) {
        if (ssidEditText.getText().length() == 0) {
            if (blame) {
                ssidEditText.setError(getString(R.string.error_no_ssid_specified));
            }
            return Optional.absent();
        } else {
            ssidEditText.setError(null);
            return Optional.of(ssidEditText.getText().toString());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Optional<String> ssid = getSelectedSsid(false);
        if (ssid.isPresent()) {
            outState.putString(SSID, ssid.get());
        }
        outState.putString(PROJECT_UUID, projectUuid);
    }

    private void fixDialogButtonsDontDismiss(final AlertDialog alertDialog) {
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            public void onShow(final DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                Optional<String> selectedSsid = getSelectedSsid(true);
                                if (selectedSsid.isPresent()) {
                                    new TriggerRepository(getActivity()).addOrReplace(projectUuid, selectedSsid.get());
                                    dismiss();
                                }
                            }
                        });
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                new TriggerRepository(getActivity()).deleteByProjectUuid(projectUuid);
                                Toast.makeText(getActivity(), R.string.toast_removed_wifi_trigger, Toast.LENGTH_LONG).show();
                                dismiss();
                            }
                        });
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                dismiss();
                            }
                        });
            }
        });
    }
}
