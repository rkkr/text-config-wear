package rkr.wear.stringblockwatch;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

public class WatchActionPickerActivity extends DialogFragment {

    public String fileName = null;
    private ImportActivity.RefreshCallback mCallback;

    public WatchActionPickerActivity() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.watch_action_picker, null);
        final ListView list = (ListView) view.findViewById(R.id.watch_action_picker);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                String item = (String) list.getItemAtPosition(position);
                switch (item) {
                    case "Import":
                        ImportActivity.ImportWatch(fileName, view.getContext());
                        getDialog().dismiss();
                        break;
                    case "Overwrite":
                        ImportActivity.ExportWatch(fileName, view.getContext());
                        mCallback.onRefreshCallback();
                        getDialog().dismiss();
                        break;
                    case "Rename":
                        FragmentManager fm = getFragmentManager();
                        WatchNamePickerActivity editNameDialog = new WatchNamePickerActivity();
                        editNameDialog.setOnRefreshCallback(mCallback);
                        editNameDialog.fileName = fileName;
                        editNameDialog.show(fm, "watch_name_picker");
                        getDialog().dismiss();
                        break;
                    case "Delete":
                        new AlertDialog.Builder(view.getContext())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage("Are you sure you want to delete watch?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ImportActivity.DeleteWatch(fileName, view.getContext());
                                        mCallback.onRefreshCallback();
                                        getDialog().dismiss();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                        break;
                }
            }
        });

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity())
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );

        builder.setView(view);
        return builder.create();
    }

    public void setOnRefreshCallback(ImportActivity.RefreshCallback callback) {
        mCallback = callback;
    }
}
