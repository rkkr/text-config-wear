package rkr.wear.stringblockwatch;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import java.util.ArrayList;

public class RowOrderPickerActivity extends DialogFragment {

    public int rowNum;

    public RowOrderPickerActivity() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.pref_order_picker, null);

        final NumberPicker list = (NumberPicker) view.findViewById(R.id.pref_order_picker);
        final ArrayList<Integer> items = Util.GetRows(view.getContext());
        list.setMinValue(1);
        list.setMaxValue(items.size());
        list.setValue(items.indexOf(rowNum) + 1);
        list.setWrapSelectorWheel(false);

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity())
                .setTitle("Set order")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                int newRowNum = list.getValue() - 1;
                                items.remove(items.indexOf(rowNum));
                                items.add(newRowNum, rowNum);
                                Util.SaveRows(view.getContext(), items);
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
