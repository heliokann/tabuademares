package com.novoideal.tabuademares.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.novoideal.tabuademares.MainActivity;
import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.model.LocationParam;

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


        private int sectionNumber;

        public PlaceholderFragment(int sectionNumber) {
            this.sectionNumber = sectionNumber;
        }

        public LocationParam getCity(LocationParam locationParam) {
            return locationParam.clone(sectionNumber);
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment(sectionNumber);
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            int position = getArguments().getInt(ARG_SECTION_NUMBER);
            MainActivity main = (MainActivity)this.getActivity();
            main.refreshAll(rootView, main.getCurrentLocation().clone(position), false);
            return rootView;
        }
    }
}
