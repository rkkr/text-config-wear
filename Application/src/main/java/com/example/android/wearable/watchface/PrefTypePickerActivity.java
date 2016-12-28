package com.example.android.wearable.watchface;

import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class PrefTypePickerActivity extends DialogFragment {

    public String rowNum;

    public PrefTypePickerActivity() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pref_type_picker, container);

        final PrefTypePickerActivity context = this;
        final ListView list = (ListView) view.findViewById(R.id.pref_type_picker);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = list.getItemAtPosition(position).toString();
                switch (selected) {
                    case "Text":
                        break;
                    case "Date/Time":
                        break;
                    default:
                        throw new Resources.NotFoundException(selected);

                }
                context.dismiss();
            }
        });

        return view;
    }
}
