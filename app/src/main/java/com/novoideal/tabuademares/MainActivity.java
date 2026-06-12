package com.novoideal.tabuademares;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import com.novoideal.tabuademares.adapter.FragmentAdapter;
import com.novoideal.tabuademares.controller.ExtremesController;
import com.novoideal.tabuademares.settings.SettingsActivity;
import com.novoideal.tabuademares.util.ThemeHelper;
import com.novoideal.tabuademares.controller.MoonController;
import com.novoideal.tabuademares.controller.SeaConditionController;
import com.novoideal.tabuademares.controller.WeatherController;
import com.novoideal.tabuademares.dao.ExtremesDao;
import com.novoideal.tabuademares.dao.SeaConditionDao;
import com.novoideal.tabuademares.dao.WeatherDao;
import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.service.LocationParamService;
import com.novoideal.tabuademares.ui.CitySearchDialog;
import com.novoideal.tabuademares.util.CityDatasetLoader;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;

import java.util.ArrayList;
import java.util.List;

import static com.novoideal.tabuademares.ui.Fragment.PlaceholderFragment;

public class MainActivity extends AppCompatActivity {

    private FragmentStatePagerAdapter mSectionsPagerAdapter;
    private List<LocationParam> locations;
    public static LocationParam currentLocation;
    private LocationParamService locationParamService;

    private ViewPager mViewPager;
    private TabLayout tabLayout;

    private static final String KEY_CURRENT_TAB = "current_tab";

    static final long REFRESH_COOLDOWN_MS = 5000L;
    private long lastRefreshAt = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = createFragmentAdapter();

        createViewPager(mSectionsPagerAdapter);

        createRefresh();
        setupTabLayout();

        if (savedInstanceState != null) {
            mViewPager.setCurrentItem(savedInstanceState.getInt(KEY_CURRENT_TAB, 0));
        }

        cleanBD();
    }

    private void setupTabLayout() {
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
        if (currentLocation.getUpdated() != null) {
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
        // Intentionally no super() call — bug on API Level > 11.
        if (mViewPager != null) {
            outState.putInt(KEY_CURRENT_TAB, mViewPager.getCurrentItem());
        }
    }

    private void createViewPager(FragmentStatePagerAdapter fragmentStatePagerAdapter) {
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(fragmentStatePagerAdapter);
        int sections = fragmentStatePagerAdapter.getCount();
        mViewPager.setOffscreenPageLimit(sections);
    }

    static boolean isSameCity(LocationParam current, LocationParam selected) {
        return current != null && selected.getName().equals(current.getName());
    }

    static boolean isCooldownActive(long now, long lastRefreshAt, long cooldownMs) {
        return now - lastRefreshAt < cooldownMs;
    }

    private void createRefresh() {
        final ImageView refresh = (ImageView) findViewById(R.id.btn_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long now = System.currentTimeMillis();
                if (isCooldownActive(now, lastRefreshAt, REFRESH_COOLDOWN_MS)) {
                    return;
                }
                lastRefreshAt = now;
                refresh.setAlpha(0.4f);
                refresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh.setAlpha(1.0f);
                    }
                }, REFRESH_COOLDOWN_MS);
                refreshOnUserIteration(true);
            }
        });
    }

    static int selectedPositionOf(List<LocationParam> locations) {
        if (locations == null) {
            return 0;
        }
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).getSelected()) {
                return i;
            }
        }
        return 0;
    }

    public FragmentStatePagerAdapter createFragmentAdapter() {
        if (locations == null) {
            locationParamService = new LocationParamService(getApplicationContext());
            try {
                locations = locationParamService.geLocations();
                if (locations == null || locations.isEmpty()) {
                    locationParamService.saveIfNew(LocationParam.defaultCity);
                    locations = locationParamService.geLocations();
                }
                int selectedPosition = selectedPositionOf(locations);
                currentLocation = locations.get(selectedPosition);
                createCitySpinner(locations, selectedPosition);
            } catch (Exception e) {
                // Falha ao restaurar a cidade salva: degrada para a cidade
                // default sem propagar o erro para a UI (RF-004 / CS-005).
                currentLocation = LocationParam.defaultCity;
                if (locations == null) {
                    locations = new ArrayList<>();
                }
                createCitySpinner(locations, selectedPositionOf(locations));
            }
        }
        return new FragmentAdapter(getSupportFragmentManager());
    }

    @SuppressLint("RestrictedApi")
    public void refreshOnUserIteration(boolean update) {
        int current = mViewPager.getCurrentItem();
        TextView cityView = (TextView) findViewById(R.id.spin_city);
        if (cityView != null && cityView.getTag() != null) {
            currentLocation = (LocationParam) cityView.getTag();
            // A seleção é persistida em onCitySelected via saveAndSelect (com o
            // id real da linha). Não regravar aqui evita zerar a flag selected
            // usando um objeto com id 0 (corrida que desfazia a seleção).
        }
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof PlaceholderFragment && fragment.getView() != null) {
                TabLayout.Tab tab = tabLayout.getTabAt(current);
                if (tab != null) tab.setText(mSectionsPagerAdapter.getPageTitle(current));
                refreshAll(fragment.getView(), ((PlaceholderFragment) fragment).getCity(currentLocation), update);
            }
        }
    }

    public LocationParam getCurrentLocation() {
        return currentLocation;
    }

    public void createCitySpinner(final List<LocationParam> cities, int selectedPosition) {
        final TextView cityView = (TextView) findViewById(R.id.spin_city);
        if (cityView == null) return;

        final LocationParam initialCity = (cities != null && !cities.isEmpty())
                ? cities.get(selectedPosition)
                : LocationParam.defaultCity;

        cityView.setText(initialCity.getName());
        cityView.setTag(initialCity);
        currentLocation = initialCity;

        final List<LocationParam> allCities = CityDatasetLoader.load(getApplicationContext());

        View.OnClickListener cityClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CitySearchDialog(MainActivity.this, allCities, new CitySearchDialog.OnCitySelectedListener() {
                    @Override
                    public void onCitySelected(LocationParam selected) {
                        if (isSameCity(currentLocation, selected)) {
                            return;
                        }
                        final LocationParam cityWithDay = selected.clone(0);
                        cityView.setText(selected.getName());
                        cityView.setTag(cityWithDay);
                        currentLocation = cityWithDay;

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                LocationParam persisted = locationParamService.saveAndSelect(cityWithDay);
                                if (persisted != null) {
                                    // Adota o id real da linha para que operações
                                    // por id (touch/getById) na sessão acertem a linha.
                                    cityWithDay.setId(persisted.getId());
                                }
                            }
                        }).start();

                        refreshOnUserIteration(false);
                    }
                }).show();
            }
        };

        View citySelector = findViewById(R.id.city_selector);
        if (citySelector != null) {
            citySelector.setOnClickListener(cityClickListener);
        } else {
            cityView.setOnClickListener(cityClickListener);
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void refreshAll(View view, LocationParam cityCondition, boolean update) {
        MoonController moonController = new MoonController(view, cityCondition);
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
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
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
