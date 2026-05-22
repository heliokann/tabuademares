package com.novoideal.tabuademares.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.model.LocationParam;

import java.util.List;

public class CitySearchDialog {

    public interface OnCitySelectedListener {
        void onCitySelected(LocationParam city);
    }

    private final Context context;
    private final List<LocationParam> cities;
    private final OnCitySelectedListener listener;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pendingFilter;

    public CitySearchDialog(Context context, List<LocationParam> cities, OnCitySelectedListener listener) {
        this.context = context;
        this.cities = cities;
        this.listener = listener;
    }

    public void show() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_city_search, null);
        EditText searchField = dialogView.findViewById(R.id.edit_city_search);
        ListView listView = dialogView.findViewById(R.id.list_cities);
        TextView noResults = dialogView.findViewById(R.id.txt_no_results);

        FilterableCityAdapter adapter = new FilterableCityAdapter(context, cities);
        listView.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.city_select_title)
                .setView(dialogView)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        searchField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (pendingFilter != null) handler.removeCallbacks(pendingFilter);
                pendingFilter = () -> {
                    adapter.getFilter().filter(s, count1 -> {
                        if (count1 == 0) {
                            listView.setVisibility(View.GONE);
                            noResults.setVisibility(View.VISIBLE);
                        } else {
                            listView.setVisibility(View.VISIBLE);
                            noResults.setVisibility(View.GONE);
                        }
                    });
                };
                handler.postDelayed(pendingFilter, 150);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationParam selected = adapter.getItem(position);
                dialog.dismiss();
                if (selected != null && listener != null) {
                    listener.onCitySelected(selected);
                }
            }
        });

        dialog.show();
    }
}
