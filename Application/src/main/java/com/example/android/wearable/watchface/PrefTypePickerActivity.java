package com.example.android.wearable.watchface;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class PrefTypePickerActivity extends DialogFragment {

    public int rowNum;

    public PrefTypePickerActivity() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pref_type_picker, container);

        final PrefTypePickerActivity context = this;
        final ListView list = (ListView) view.findViewById(R.id.pref_type_picker);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selected = list.getItemAtPosition(position).toString();
                int itemNum = Util.AddRowItem(getView().getContext(), rowNum, selected);

                switch (selected) {
                    case "Text":
                        Intent intent = new Intent(getView().getContext(), SettingsTextActivity.class);
                        intent.putExtra("ROW_ID", rowNum);
                        intent.putExtra("ITEM_ID", itemNum);
                        getView().getContext().startActivity(intent);
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
