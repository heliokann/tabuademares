package com.novoideal.tabuademares.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.model.SeaCondition;

/**
 * Created by Helio on 23/11/2017.
 */

public class SeaConditionViewAdapter extends BaseAdapter {

    private View view;
    private SeaCondition[] seaConditions;

    public SeaConditionViewAdapter(View view, SeaCondition[] seaConditions){
        this.view = view;
        this.seaConditions = seaConditions;
    }

    @Override
    public int getCount() {
        return seaConditions.length;
    }

    @Override
    public Object getItem(int position) {
        return seaConditions[position];
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

            gridView = inflater.inflate(R.layout.item_swell, null);
            SeaCondition seaCondition = seaConditions[position];

            TextView agitation = (TextView) gridView.findViewById(R.id.swell_agitation);
            agitation.setText(seaCondition.getAgitation());

            TextView height = (TextView) gridView.findViewById(R.id.swell_height);
            height.setText(seaCondition.getFullSwell());

            TextView period = (TextView) gridView.findViewById(R.id.swell_period);
            period.setText(seaCondition.getPeriod());

            TextView wind = (TextView) gridView.findViewById(R.id.swell_wind);
            wind.setText(seaCondition.getWindStr());

//            ImageView iv = gridView.findViewById(R.id.extreme_type_icon);
//            iv.setImageResource(seaCondition.isLow() ? R.drawable.down : R.drawable.up);

        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }
}
