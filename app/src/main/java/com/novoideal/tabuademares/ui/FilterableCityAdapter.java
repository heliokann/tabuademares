package com.novoideal.tabuademares.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.util.TextNormalizer;

import java.util.ArrayList;
import java.util.List;

public class FilterableCityAdapter extends ArrayAdapter<LocationParam> {

    private final List<LocationParam> allCities;
    private List<LocationParam> filtered;
    private final CityFilter filter = new CityFilter();

    public FilterableCityAdapter(Context context, List<LocationParam> cities) {
        super(context, R.layout.item_city_search, new ArrayList<>(cities));
        this.allCities = new ArrayList<>(cities);
        this.filtered = new ArrayList<>(cities);
    }

    @Override
    public int getCount() {
        return filtered.size();
    }

    @Override
    public LocationParam getItem(int position) {
        return filtered.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_city_search, parent, false);
        }
        TextView tvName = convertView.findViewById(R.id.tv_city_name);
        TextView tvState = convertView.findViewById(R.id.tv_city_state);

        String full = filtered.get(position).getName();
        int sep = full.lastIndexOf(" - ");
        tvName.setText(sep >= 0 ? full.substring(0, sep) : full);
        tvState.setText(sep >= 0 ? full.substring(sep + 3) : "");

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private class CityFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = new ArrayList<>(allCities);
                results.count = allCities.size();
            } else {
                String normalized = normalize(constraint.toString());
                List<LocationParam> matches = new ArrayList<>();
                for (LocationParam city : allCities) {
                    if (normalize(city.getName()).contains(normalized)) {
                        matches.add(city);
                    }
                }
                results.values = matches;
                results.count = matches.size();
            }
            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered = (List<LocationParam>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

        private String normalize(String input) {
            return TextNormalizer.normalize(input);
        }
    }
}
