package com.novoideal.tabuademares.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.model.LocationParam;

import java.util.List;

/**
 * Created by Helio on 29/12/2017.
 */

public class LocaleSpinnerAdapter extends ArrayAdapter<LocationParam> {

    public LocaleSpinnerAdapter(Context context, List<LocationParam> locales) {
        super(context, R.layout.spinner_item, locales);
        setDropDownViewResource(R.layout.spinner_dropdown_item);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
//return super.getView(position, convertView, parent);
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View row = inflater.inflate(R.layout.spinner_item, parent, false);
        TextView label = (TextView) row.findViewById(R.id.spinner_item);
        LocationParam locationParam = this.getItem(position);
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