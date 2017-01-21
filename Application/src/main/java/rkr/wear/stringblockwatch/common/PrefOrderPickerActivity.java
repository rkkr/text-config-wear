package rkr.wear.stringblockwatch.common;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import java.util.ArrayList;

import rkr.wear.stringblockwatch.R;

public class PrefOrderPickerActivity extends DialogFragment {

    public int itemNum;
    public int rowNum;
    public SettingsManager mSettings;

    public PrefOrderPickerActivity() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.pref_order_picker, null);

        final NumberPicker list = (NumberPicker) view.findViewById(R.id.pref_order_picker);
        final ArrayList<Integer> items = mSettings.GetRowItems(rowNum);
        list.setMinValue(1);
        list.setMaxValue(items.size());
        list.setValue(items.indexOf(itemNum) + 1);
        list.setWrapSelectorWheel(false);

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity())
                .setTitle("Set order")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                int newItemNum = list.getValue() - 1;
                                items.remove(items.indexOf(itemNum));
                                items.add(newItemNum, itemNum);
                                mSettings.SaveRowItems(rowNum, items);
                                dialog.dismiss();
                            }
                        }
                )
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
}
