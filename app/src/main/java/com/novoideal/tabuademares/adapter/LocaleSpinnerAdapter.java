package com.novoideal.tabuademares.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.novoideal.tabuademares.MainActivity;
import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.model.LocationParam;

import java.util.List;

/**
 * Created by Helio on 29/12/2017.
 */

public class LocaleSpinnerAdapter extends ArrayAdapter<LocationParam> {

    public LocaleSpinnerAdapter(Context context, List<LocationParam> locales) {
        super(context, R.layout.spinner_item, locales);
        locales.add(null);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent, R.layout.spinner_dropdown_item, R.id.spinner_dropdown);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItem(position) == null) {
            position = getPosition(MainActivity.currentLocation);
        }

        return getCustomView(position, convertView, parent, R.layout.spinner_item, R.id.spinner_item);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent, int resource, int idRow) {
//return super.getView(position, convertView, parent);
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LocationParam locationParam = this.getItem(position);
        if (locationParam == null) {
            return inflater.inflate(R.layout.spinner_new_item, parent, false);
        }

        View row = inflater.inflate(resource, parent, false);
        TextView label = (TextView) row.findViewById(idRow);
        label.setText(locationParam.toString());

//        ImageView icon = (ImageView) row.findViewById(R.id.icon);
//
//        if (locationParam == "Sunday") {
//            icon.setImageResource(R.drawable.icon);
//        } else {
//            icon.setImageResource(R.drawable.icongray);
//        }

        return row;
    }
}