package com.novoideal.tabuademares.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.novoideal.tabuademares.MainActivity;
import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.model.LocationParam;

import java.util.List;

/**
 * Created by Helio on 04/12/2017.
 */

public class Fragment {
    /**
     * A placeholder fragment containing a simple view.
     */
    @SuppressLint("ValidFragment")
    public static class PlaceholderFragment extends android.support.v4.app.Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";


        private List<LocationParam> cities;

        public PlaceholderFragment(int sectionNumber, List<LocationParam> cities) {
            this.cities =  cities;
        }

        public List<LocationParam> getCities() {
            return cities;
        }

        public static PlaceholderFragment newInstance(int sectionNumber, List<LocationParam> cities) {
            PlaceholderFragment fragment = new PlaceholderFragment(sectionNumber, cities);
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//            TextView textView = rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.sessionTitle));
            int position = getArguments().getInt(ARG_SECTION_NUMBER);
//                Toast.makeText(container.getContext(), "Session " + position, Toast.LENGTH_LONG).show();
            MainActivity main = (MainActivity)this.getActivity();
//            PlaceholderFragment fragment = (PlaceholderFragment) main.mSectionsPagerAdapter.instantiateItem(main.mViewPager, position);
            PlaceholderFragment fragment = (PlaceholderFragment) main.getSectionsPagerAdapter().instantiateItem(main.getViewPager(), position);
            main.createCitySpinner(rootView, fragment.cities);
            return rootView;
        }
    }
}
