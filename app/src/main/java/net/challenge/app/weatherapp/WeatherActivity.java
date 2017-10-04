
package net.challenge.app.weatherapp;


import android.app.ProgressDialog;

import android.content.Intent;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;
import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.challenge.app.weatherapp.data.Channel;
import net.challenge.app.weatherapp.data.Condition;
import net.challenge.app.weatherapp.data.Units;
import net.challenge.app.weatherapp.fragments.WeatherConditionFragment;
import net.challenge.app.weatherapp.listener.WeatherServiceListener;
import net.challenge.app.weatherapp.service.WeatherCacheService;
import net.challenge.app.weatherapp.service.WundergroundService;

public class WeatherActivity extends AppCompatActivity implements WeatherServiceListener {


    private ImageView weatherIconImageView;
    private TextView temperatureTextView;
    private TextView conditionTextView;
    private TextView locationTextView;
    private WundergroundService weatherService;
    private WeatherCacheService cacheService;
    private ProgressDialog loadingDialog;

    // weather service fail flag
    private boolean weatherServicesHasFailed = false;

    private SharedPreferences preferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        weatherIconImageView = (ImageView) findViewById(R.id.weatherIconImageView);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        conditionTextView = (TextView) findViewById(R.id.conditionTextView);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        weatherService = new WundergroundService(this);
        weatherService.setTemperatureUnit(preferences.getString(getString(R.string.pref_temperature_unit), null));


        cacheService = new WeatherCacheService(this);

        if (preferences.getBoolean(getString(R.string.pref_needs_setup), true)) {
            startSettingsActivity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getString(R.string.loading));
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        String location;

            location = preferences.getString(getString(R.string.pref_manual_location), null);

        if (location != null) {
            weatherService.refreshWeather(location);
            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null) {
                actionBar.setTitle(location);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void serviceSuccess(Channel channel) {
        loadingDialog.hide();
        Condition condition = channel.getItem().getCondition();
        Units units = channel.getUnits();
        Condition[] forecast = channel.getItem().getForecast();
        int weatherIconImageResource = getResources().getIdentifier("icon_" + condition.getCode(), "drawable", getPackageName());
        weatherIconImageView.setImageResource(weatherIconImageResource);
        temperatureTextView.setText(getString(R.string.temperature_output, condition.getTemperature(), units.getTemperature()));


        //Change background color
        String unitChoose = String.valueOf(units.getTemperature());

        if(unitChoose.equals("F")) {

            Integer num1 = Integer.valueOf(condition.getTemperature());

            RelativeLayout layerHeader = (RelativeLayout) findViewById(R.id.rootLayout);

            if (num1 > 60) {


                layerHeader.setBackgroundColor(getResources().getColor(R.color.hot));
            } else {
                layerHeader.setBackgroundColor(getResources().getColor(R.color.cold));

            }
        }

        if(unitChoose.equals("C")) {

            Integer num1 = Integer.valueOf(condition.getTemperature());

            RelativeLayout layerHeader = (RelativeLayout) findViewById(R.id.rootLayout);

            if (num1 > 16) {


                layerHeader.setBackgroundColor(getResources().getColor(R.color.hot));
            } else {
                layerHeader.setBackgroundColor(getResources().getColor(R.color.cold));

            }
        }

        conditionTextView.setText(condition.getDescription());
        //locationTextView.setText(channel.getLocation());


        for (int day = 0; day < forecast.length; day++) {
            if (day >= 5) {
                break;
            }

            Condition currentCondition = forecast[day];

            int viewId = getResources().getIdentifier("forecast_" + day, "id", getPackageName());
            WeatherConditionFragment fragment = (WeatherConditionFragment) getSupportFragmentManager().findFragmentById(viewId);

            if (fragment != null) {
                fragment.loadForecast(currentCondition, channel.getUnits());
            }
        }

        cacheService.save(channel);
    }

    @Override
    public void serviceFailure(Exception exception) {
        if (weatherServicesHasFailed) {
            loadingDialog.hide();
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            weatherServicesHasFailed = true;
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();

            cacheService.load(this);
        }
    }

}
