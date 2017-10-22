package com.novoideal.tabuademares;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.novoideal.tabuademares.controller.ExtremesController;
import com.novoideal.tabuademares.controller.MoonController;
import com.novoideal.tabuademares.controller.SeaConditionController;
import com.novoideal.tabuademares.controller.WindController;
import com.novoideal.tabuademares.controller.base.AbstractController;
import com.novoideal.tabuademares.service.CityCondition;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    public Date date = new Date();

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static MainActivity mainActivity;
    private CityCondition currentCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton refresh = (FloatingActionButton) findViewById(R.id.btn_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   refreshAll(null, currentCity);
               }
        });
    }

    public void createCitySpinner(View rootView){
        List<CityCondition> defaultCities = new ArrayList<>();
        defaultCities.add(CityCondition.defaultCity);
        defaultCities.add(new CityCondition(3464, 455891, "Niteroi",
                LocalDate.now().toDate(), -22.909309, -43.072231));


        ArrayAdapter<CityCondition> arrayAdapter = new ArrayAdapter<CityCondition>(getApplicationContext(), android.R.layout.simple_spinner_item, defaultCities);
        final Spinner cities = (Spinner) rootView.findViewById(R.id.spin_city);
        AdapterView.OnItemSelectedListener choose = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentCity = (CityCondition) cities.getSelectedItem();
                AbstractController.clearCache();
                SeaConditionController.clearCache();
                mainActivity.refreshAll(mainActivity.mViewPager, currentCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

        cities.setAdapter(arrayAdapter);
        cities.setOnItemSelectedListener(choose);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void refreshAll(View rootView, CityCondition cityCondition) {
        if (rootView == null) {
            AbstractController.clearCache();
            SeaConditionController.clearCache();
            Toast.makeText(mainActivity, getString(R.string.refreshing), Toast.LENGTH_LONG).show();
        }

        new MoonController(rootView).request(cityCondition);
        new WindController(rootView).request();
        new ExtremesController(rootView).request(cityCondition);
        new SeaConditionController(rootView).request(cityCondition);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Snackbar.make(mainActivity.mViewPager, "Ainda não implementado", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return true;
        }
        if (id == R.id.action_about) {
            Toast.makeText(mainActivity.mViewPager.getContext(), getString(R.string.author), Toast.LENGTH_LONG).show();
            return true;
        }

        Snackbar.make(mainActivity.mViewPager, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        static PlaceholderFragment cache;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
//            if(cache != null){
//                return cache;
//            }
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            cache = fragment;
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.sessionTitle, new Date()));

//            LocalDate dt = LocalDate.now();
//            Double age = MoonIllumination.of(dt.toDate()).getPhase() * 29.5308;
//            textView = (TextView) rootView.findViewById(R.id.moon_phase);
//            textView.setText(getString(R.string.moon_phase, "quarter", age));

            System.out.println("Entrou no OnCreateView");
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    MainActivity.mainActivity.refreshAll(rootView, mainActivity.currentCity);
//                }
//            };

//            Thread thread = new Thread(runnable);
//            thread.start();

            mainActivity.createCitySpinner(rootView);

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 1 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
