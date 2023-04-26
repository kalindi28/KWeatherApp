package com.virutsa.code.weatherapp;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.virutsa.code.weatherapp.constants.Constant;
import com.virutsa.code.weatherapp.databinding.ActivityMainBinding;
import com.virutsa.code.weatherapp.helper.permissions.Permission;
import com.virutsa.code.weatherapp.helper.permissions.PermissionHandler;
import com.virutsa.code.weatherapp.model.Weather;
import com.virutsa.code.weatherapp.util.NetworkUtils;
import com.virutsa.code.weatherapp.util.Utils;
import com.virutsa.code.weatherapp.viewmodel.WeatherViewModel;
import com.weatherapidemo.model.City;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WeatherActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;
    private WeatherViewModel mViewModel;
    private Double latitude = 44.34;
    private Double longitude = 10.99;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        mViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        checkPermissions();


        observeCityChanges();
        observeWeatherDataChanges();

        setOnClickListner();
    }

    private void checkDataExists() {
        if (WeatherSession.getInstance().getCityData(WeatherActivity.this) != null) {
            latitude = WeatherSession.getInstance().getCityData(WeatherActivity.this).getLat();
            longitude = WeatherSession.getInstance().getCityData(WeatherActivity.this).getLon();
            setCityDataToView(WeatherSession.getInstance().getCityData(WeatherActivity.this));

        } else {
            if (NetworkUtils.isConnected(WeatherActivity.this)) {
                mViewModel.getWeatherData(latitude, longitude);
            } else {
                Toast.makeText(WeatherActivity.this, getString(R.string.internet_connection_lost), Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void setCityDataToView(City city) {
        WeatherSession.getInstance().setCityData(city, WeatherActivity.this);
        mainBinding.cityName.setText(city.getName());
        mainBinding.countryName.setText(String.format("%s,%s", city.getState(), city.getCountry()));
        latitude = city.getLat();
        longitude = city.getLon();
        if (NetworkUtils.isConnected(WeatherActivity.this)) {
            mViewModel.getWeatherData(city.getLat(), city.getLon());
        } else {
            Toast.makeText(WeatherActivity.this, getString(R.string.internet_connection_lost), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissions() {
        PermissionHandler permissionHandler = new PermissionHandler(WeatherActivity.this);
        if(!permissionHandler.isPermissionGranted(Permission.LOCATION)) {
            permissionHandler.askForPermission(Permission.LOCATION,this);
        }else {

            checkDataExists();
        }

       }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @ NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    // boolean isGranted=   permissionHandler.checkCrucialPermissionsGranted(this, requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (WeatherSession.getInstance().getCityData(WeatherActivity.this) != null) {
                latitude = WeatherSession.getInstance().getCityData(WeatherActivity.this).getLat();
                longitude = WeatherSession.getInstance().getCityData(WeatherActivity.this).getLon();
                setCityDataToView(WeatherSession.getInstance().getCityData(WeatherActivity.this));

            } else {
                if (NetworkUtils.isConnected(WeatherActivity.this)) {
                    mViewModel.getWeatherData(latitude, longitude);
                } else {
                    Toast.makeText(WeatherActivity.this, getString(R.string.internet_connection_lost), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (WeatherSession.getInstance().getCityData(WeatherActivity.this) != null) {
                latitude = WeatherSession.getInstance().getCityData(WeatherActivity.this).getLat();
                longitude = WeatherSession.getInstance().getCityData(WeatherActivity.this).getLon();
                setCityDataToView(WeatherSession.getInstance().getCityData(WeatherActivity.this));

            }

        }
    }

    private void setOnClickListner() {
        mainBinding.btnSearch.setOnClickListener(view -> {
            if (!Objects.requireNonNull(mainBinding.edtQueryCity.getText()).toString().trim().isEmpty()) {
                if(NetworkUtils.isConnected(WeatherActivity.this)) {
                    mViewModel.getCityLatLongBySearch(mainBinding.edtQueryCity.getText().toString());
                }else {
                    Toast.makeText(WeatherActivity.this, getString(R.string.internet_connection_lost), Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(WeatherActivity.this, getString(R.string.enter_city_msg), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeWeatherDataChanges() {
        mViewModel.getWeatherDataObserver().observe(WeatherActivity.this, weatherResponse -> {
            Weather weather = weatherResponse.getWeathers().get(0);
            mainBinding.weatherSummary.setText(weather.getDescription());
            mainBinding.weatherTitle.setText(weather.getMain());
            double temp = weatherResponse.getTemprature().getTemp();
            temp = temp - 273.15;
            mainBinding.tempratureDetails.setText(String.format("%s%s ℃", getString(R.string.temprature), Utils.dblValue("" + temp)));
            Glide.with(WeatherActivity.this)
                    .load(Constant.BASE_URL_WEATHER_ICON + weather.getIcon() + ".png")
                    .into(mainBinding.weatherDisplayIcon);

        });
    }

    private void observeCityChanges() {
        mViewModel.getCityDataObserver().observe(WeatherActivity.this, cities -> {
            if (cities != null && cities.size() > 0) {
                City city = cities.get(0);
                setCityDataToView(city);
            }
        });
    }
}
