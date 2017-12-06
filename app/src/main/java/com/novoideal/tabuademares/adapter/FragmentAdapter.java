package com.novoideal.tabuademares.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.joda.time.LocalDate;

/**
 * Created by Helio on 23/11/2017.
 */

public class FragmentAdapter extends FragmentStatePagerAdapter {

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        return com.novoideal.tabuademares.ui.Fragment.PlaceholderFragment.newInstance(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return new LocalDate().plusDays(position).toString("dd/MM/yyyy");
    }
}
