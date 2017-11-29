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

/**
 * Created by Helio on 23/11/2017.
 */

public class ExtremeViewAdapter extends BaseAdapter {

    private View view;
    private List<ExtremeTide> today;

    public ExtremeViewAdapter(View view, List<ExtremeTide> today){
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

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            gridView = inflater.inflate(R.layout.item_extreme, null);
            ExtremeTide extreme = today.get(position);

            TextView extremeType = (TextView) gridView.findViewById(R.id.extreme_type);
            extremeType.setText(extreme.getType());

            TextView extremeTime = (TextView) gridView.findViewById(R.id.extreme_time);
            extremeTime.setText(extreme.getStrHourMinute());

            TextView extremeHeight = (TextView) gridView.findViewById(R.id.extreme_height);
            extremeHeight.setText(extreme.getStrHeight());

            ImageView iv = gridView.findViewById(R.id.extreme_type_icon);
            iv.setImageResource(extreme.isLow() ? R.drawable.down : R.drawable.up);

        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }
}
