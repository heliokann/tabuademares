package com.novoideal.tabuademares.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.model.ExtremeTide;

import java.util.List;

public class ExtremeViewAdapter extends BaseAdapter {

    private View view;
    private List<ExtremeTide> today;

    public ExtremeViewAdapter(View view, List<ExtremeTide> today) {
        this.view = view;
        this.today = today;
    }

    @Override
    public int getCount() {
        return today.size();
    }

    @Override
    public Object getItem(int position) {
        return today.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = view.getContext();
        View gridView = convertView != null
                ? convertView
                : LayoutInflater.from(context).inflate(R.layout.item_extreme, parent, false);

        ExtremeTide extreme = today.get(position);

        ((TextView) gridView.findViewById(R.id.extreme_type))
                .setText(extreme.isLow() ? R.string.low_water : R.string.hight_tide);
        ((TextView) gridView.findViewById(R.id.extreme_time))
                .setText(extreme.getStrHourMinute());
        ((TextView) gridView.findViewById(R.id.extreme_height))
                .setText(extreme.getStrHeight());
        ((ImageView) gridView.findViewById(R.id.extreme_type_icon))
                .setImageResource(extreme.isLow() ? R.drawable.tide_down : R.drawable.tide_hight);

        return gridView;
    }
}
