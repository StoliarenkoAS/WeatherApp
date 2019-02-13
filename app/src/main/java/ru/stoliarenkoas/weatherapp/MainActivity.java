package ru.stoliarenkoas.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ArrayList<WeatherCard> cards;
    WeatherCardAdapter adapter;

    private boolean weatherVisible;
    private View weatherFragmentView;
    private View selectionFragmentView;

    private String cityName;
    private String currentWeather;

    private boolean showHumidity;
    private boolean showPressure;
    private boolean showTemperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        weatherVisible = getWeatherFragmentVisibility();
        CityWeatherFragment weatherFragment = (CityWeatherFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_city_weather);
        cards = weatherFragment.getCards();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new WeatherCardAdapter(cards);
        adapter.setOnItemClickListener(new WeatherCardAdapter.OnItemClickListener() {
            @Override
            public void onLongClick(View view, int position) {
                cards.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getParameters();
        updateWeather();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(newBase);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (weatherFragmentView.getVisibility() == View.VISIBLE) {
            selectionFragmentView.setVisibility(View.VISIBLE);
            weatherFragmentView.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new AsyncTask<Integer, Integer, Object>() {
            Integer i;

            @Override
            protected void onProgressUpdate(Integer... values) {
                System.out.println("Current num is: " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected Object doInBackground(Integer... integers) {
                i = integers[0];
                while (i > 0) {
                    publishProgress(i--);}
                return null;
            }
        }.execute(10);

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings pressed!", Toast.LENGTH_LONG).show();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean getWeatherFragmentVisibility() {
        selectionFragmentView = findViewById(R.id.fragment_city_selection);
        if ("ghosty".equals(((View)selectionFragmentView.getParent()).getTag())) {
            weatherFragmentView = findViewById(R.id.fragment_city_weather);
            weatherFragmentView.setVisibility(View.INVISIBLE);
            return false;
        }
        return true;
    }

    private void updateWeather() {
        StringBuilder sb = new StringBuilder("Storming clouds");
        if (showTemperature) sb.append(", 271K");
        if (showHumidity) sb.append(", 31%");
        if (showPressure) sb.append(", 760mm");
        sb.append(".");
        currentWeather = sb.toString();
    }

    private void getParameters() {
        cityName = ((TextView)findViewById(R.id.settings_input_city)).getText().toString();
        if (cityName.isEmpty()) cityName = "Manhattan";

        showHumidity = ((Switch)findViewById(R.id.switch_show_humidity)).isChecked();
        showPressure = ((Switch)findViewById(R.id.switch_show_pressure)).isChecked();
        showTemperature = ((Switch)findViewById(R.id.switch_show_temperature)).isChecked();
    }

    public void confirmSelection(View view) {
        getParameters();
        updateWeather();

        if (!weatherVisible) {
            selectionFragmentView.setVisibility(View.INVISIBLE);
            weatherFragmentView.setVisibility(View.VISIBLE);
        }

        cards.add(0, new WeatherCard(cityName, currentWeather, R.drawable.cloudy));
        adapter.notifyItemInserted(0);
    }
}
