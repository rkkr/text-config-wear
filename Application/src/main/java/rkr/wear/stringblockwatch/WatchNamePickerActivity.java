package rkr.wear.stringblockwatch;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WatchNamePickerActivity extends DialogFragment {

    public String fileName = null;
    private ImportActivity.RefreshCallback mCallback;

    public WatchNamePickerActivity() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.watch_name_picker, null);
        final EditText editText = (EditText) view.findViewById(R.id.watch_name_text);
        if (fileName != null)
            editText.setText(fileName);

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity())
                .setTitle("Watch name")
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface d) {

                Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = editText.getText().toString();
                        //If not specified
                        if (text.equals("")) {
                            Toast.makeText(view.getContext(), "Invalid file name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //Same name
                        if (fileName != null && text.equals(fileName)) {
                            dialog.dismiss();
                            return;
                        }
                        //Update name
                        if (fileName != null) {
                            if (ImportActivity.ListSavedWatched(view.getContext()).contains(text)) {
                                Toast.makeText(view.getContext(), "Duplicate file name", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            ImportActivity.RenameWatch(fileName, text, view.getContext());
                            mCallback.onRefreshCallback();
                            dialog.dismiss();
                            return;
                        }
                        //Save new
                        if (fileName == null) {
                            if (ImportActivity.ListSavedWatched(view.getContext()).contains(text)) {
                                Toast.makeText(view.getContext(), "Duplicate file name", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            ImportActivity.ExportWatch(text, view.getContext());
                            mCallback.onRefreshCallback();
                            dialog.dismiss();
                            return;
                        }
                    }
                });
            }
        });
        return dialog;
    }

    public void setOnRefreshCallback(ImportActivity.RefreshCallback callback) {
        mCallback = callback;
    }

}
