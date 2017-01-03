package rkr.wear.stringblockwatch;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

public class RowPaddingPickerActivity extends DialogFragment {

    public int rowNum;
    private static final String[] paddingList = new String[]{"0", "5", "10", "20", "40", "80", "160"};

    public RowPaddingPickerActivity() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.pref_padding_picker, null);

        final NumberPicker leftList = (NumberPicker) view.findViewById(R.id.pref_padding_left_picker);
        final NumberPicker bottomList = (NumberPicker) view.findViewById(R.id.pref_padding_bottom_picker);
        final NumberPicker rightList = (NumberPicker) view.findViewById(R.id.pref_padding_right_picker);
        final NumberPicker topList = (NumberPicker) view.findViewById(R.id.pref_padding_top_picker);

        leftList.setMinValue(0);
        bottomList.setMinValue(0);
        rightList.setMinValue(0);
        topList.setMinValue(0);

        leftList.setMaxValue(paddingList.length - 1);
        bottomList.setMaxValue(paddingList.length - 1);
        rightList.setMaxValue(paddingList.length - 1);
        topList.setMaxValue(paddingList.length - 1);

        leftList.setDisplayedValues(paddingList);
        bottomList.setDisplayedValues(paddingList);
        rightList.setDisplayedValues(paddingList);
        topList.setDisplayedValues(paddingList);

        leftList.setWrapSelectorWheel(false);
        bottomList.setWrapSelectorWheel(false);
        rightList.setWrapSelectorWheel(false);
        topList.setWrapSelectorWheel(false);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        final int leftListValue = indexOf(prefs.getString("row_" + rowNum + "_padding_left", "0"));
        final int bottomListValue = indexOf(prefs.getString("row_" + rowNum + "_padding_bottom", "0"));
        final int rightListValue = indexOf(prefs.getString("row_" + rowNum + "_padding_right", "0"));
        final int topListValue = indexOf(prefs.getString("row_" + rowNum + "_padding_top", "0"));

        leftList.setValue(leftListValue);
        bottomList.setValue(bottomListValue);
        rightList.setValue(rightListValue);
        topList.setValue(topListValue);

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity())
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (leftList.getValue() != leftListValue)
                                    prefs.edit().putString("row_" + rowNum + "_padding_left", paddingList[leftList.getValue()]).apply();
                                if (bottomList.getValue() != bottomListValue)
                                    prefs.edit().putString("row_" + rowNum + "_padding_bottom", paddingList[bottomList.getValue()]).apply();
                                if (rightList.getValue() != rightListValue)
                                    prefs.edit().putString("row_" + rowNum + "_padding_right", paddingList[rightList.getValue()]).apply();
                                if (topList.getValue() != topListValue)
                                    prefs.edit().putString("row_" + rowNum + "_padding_top", paddingList[topList.getValue()]).apply();
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

    private int indexOf(String item)
    {
        for (int i=0; i<paddingList.length; i++)
            if (paddingList[i].equals(item))
                return i;
        return 0;
    }
}
