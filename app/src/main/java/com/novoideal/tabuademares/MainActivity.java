package com.novoideal.tabuademares;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.novoideal.tabuademares.adapter.FragmentAdapter;
import com.novoideal.tabuademares.adapter.LocaleSpinnerAdapter;
import com.novoideal.tabuademares.controller.ExtremesController;
import com.novoideal.tabuademares.controller.MoonController;
import com.novoideal.tabuademares.controller.SeaConditionController;
import com.novoideal.tabuademares.controller.WeatherController;
import com.novoideal.tabuademares.dao.ExtremesDao;
import com.novoideal.tabuademares.dao.SeaConditionDao;
import com.novoideal.tabuademares.dao.WeatherDao;
import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.service.LocationParamService;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;

import java.util.List;

import static com.novoideal.tabuademares.ui.Fragment.PlaceholderFragment;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private FragmentStatePagerAdapter mSectionsPagerAdapter;
    private List<LocationParam> locations;
    public static LocationParam currentLocation;
    private LocationParamService locationParamService;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private  TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = createFragmentAdapter();

        createViewPager(mSectionsPagerAdapter);

        createRefresh();
        setupTabLayout();

        cleanBD();
    }

    private void setupTabLayout(){
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void cleanBD() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LocalDate now = new LocalDate();
                new SeaConditionDao(getApplicationContext()).clearBefore(now);
                new ExtremesDao(getApplicationContext()).clearBefore(now);
                new WeatherDao(getApplicationContext()).clearBefore(now);
            }
        }).start();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        DateTime now = new DateTime();
        if(currentLocation.getUpdated() != null){
            DateTime dt_updated = new DateTime(currentLocation.getUpdated());
            if (Minutes.minutesBetween(dt_updated, now).getMinutes() < 3) {
                ((TextView) findViewById(R.id.date_refresh)).setText(dt_updated.toString("dd/MM/yyyy HH:mm"));
                return;
            }
        }

        DateTime updated = new DateTime(locationParamService.getLastUpdated(currentLocation));
        String str_updated = updated.toString("dd/MM/yyyy HH:mm");

        if (Hours.hoursBetween(updated, now).getHours() > 3) {
            str_updated = now.toString("dd/MM/yyyy HH:mm");
            currentLocation.setUpdated(now.toDate());
            locationParamService.touch(currentLocation);
            refreshOnUserIteration(true);
        }
        ((TextView) findViewById(R.id.date_refresh)).setText(str_updated);
    }

    @Override
    @SuppressLint("MissingSuperCall")
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    private void createViewPager(FragmentStatePagerAdapter fragmentStatePagerAdapter) {
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(fragmentStatePagerAdapter);
        int sections = fragmentStatePagerAdapter.getCount();
        mViewPager.setOffscreenPageLimit(sections);
    }

    private void createRefresh() {
        ImageView refresh = (ImageView) findViewById(R.id.btn_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshOnUserIteration(true);
            }
        });
    }

    private int getSelectedPosition() {
        int position = 0;
        for (int i = 0; i < locations.size(); i++) {
            if (Boolean.TRUE.equals(locations.get(i).getSelected())) {
                position = i;
                break;
            }
        }
        return position;
    }

    public FragmentStatePagerAdapter createFragmentAdapter(){
        if (locations == null){
            locationParamService = new LocationParamService(getApplicationContext());
            locations = locationParamService.geLocations();
            int selectedPosition = getSelectedPosition();
            currentLocation = locations.get(selectedPosition);
            createCitySpinner(locations, selectedPosition);
        }

        return new FragmentAdapter(getSupportFragmentManager());
    }

    @SuppressLint("RestrictedApi")
    public void refreshOnUserIteration(boolean update) {
        int current = mViewPager.getCurrentItem();
        Spinner spinner = (Spinner) findViewById(R.id.spin_city);

//        if(spinner.getSelectedItem() == null) {
//            Toast.makeText(getApplicationContext(), "Criar nova activity", Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(this, SearchLocation.class);
//            startActivity(intent);
////            intent.on
//            return;
//        }

        currentLocation = (LocationParam) spinner.getSelectedItem();
        locationParamService.updateSelected(currentLocation);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            tabLayout.getTabAt(current).setText(mSectionsPagerAdapter.getPageTitle(current));
            refreshAll(fragment.getView(), ((PlaceholderFragment) fragment).getCity(currentLocation), update);
        }
    }

    public LocationParam getCurrentLocation() {
        return currentLocation;
    }

    public void createCitySpinner(List<LocationParam> cities, int selectedPosition) {
//        ArrayAdapter<LocationParam> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, cities);
//        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        final Spinner spinner = (Spinner) findViewById(R.id.spin_city);
        spinner.setAdapter(new LocaleSpinnerAdapter(getApplicationContext(), cities));
//        spinner.setAdapter(arrayAdapter);
        spinner.setSelected(false);
        spinner.setSelection(selectedPosition,false);

        AdapterView.OnItemSelectedListener choose = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                refreshOnUserIteration(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

        spinner.setOnItemSelectedListener(choose);
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

    public void refreshAll(View view, LocationParam cityCondition, boolean update) {
        MoonController moonController = new MoonController(view,cityCondition);
        ExtremesController extremesController = new ExtremesController(view, cityCondition);
        SeaConditionController seaConditionController = new SeaConditionController(view, cityCondition);
        WeatherController weatherController = new WeatherController(view, cityCondition);

        if (update) {
            Toast.makeText(getApplicationContext(), "Atualizando: " + cityCondition.getTodayStr(), Toast.LENGTH_LONG).show();
            extremesController.update();
            seaConditionController.update();
            weatherController.update();
        }

        moonController.request();
        extremesController.request();
        seaConditionController.request();
        weatherController.request();
    }

    public FragmentStatePagerAdapter getSectionsPagerAdapter() {
        return mSectionsPagerAdapter;
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Snackbar.make(this.mViewPager, "Ainda não implementado", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return true;
        }
        if (id == R.id.action_about) {
            Toast.makeText(getApplicationContext(), getString(R.string.author), Toast.LENGTH_LONG).show();
            return true;
        }

        Snackbar.make(this.mViewPager, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        return super.onOptionsItemSelected(item);
    }


}
