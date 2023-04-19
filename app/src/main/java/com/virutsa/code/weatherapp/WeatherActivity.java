package com.virutsa.code.weatherapp;

import static com.virutsa.code.weatherapp.constants.Constant.MULTIPLE_PERMISSIONS;
import static com.virutsa.code.weatherapp.constants.Constant.permissions;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.virutsa.code.weatherapp.constants.Constant;
import com.virutsa.code.weatherapp.databinding.ActivityMainBinding;
import com.virutsa.code.weatherapp.factory.WeatherViewModelFactory;
import com.virutsa.code.weatherapp.model.Weather;
import com.virutsa.code.weatherapp.repository.WeatherRepository;
import com.virutsa.code.weatherapp.util.NetworkUtils;
import com.virutsa.code.weatherapp.util.Utils;
import com.virutsa.code.weatherapp.viewmodel.WeatherViewModel;
import com.weatherapidemo.model.City;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        mViewModel = new ViewModelProvider(this, new WeatherViewModelFactory(new WeatherRepository())).get(WeatherViewModel.class);
        checkPermissions();

        checkDataExists();
        observeCityChanges();
        observeWeatherDataChanges();

        setOnClickListner();
    }

    private void checkDataExists() {
        if (WeatherSession.getInstance().getCityData(WeatherActivity.this) != null) {
            latitude = WeatherSession.getInstance().getCityData(WeatherActivity.this).getLat();
            longitude = WeatherSession.getInstance().getCityData(WeatherActivity.this).getLon();
            setCityDataToView(WeatherSession.getInstance().getCityData(WeatherActivity.this));
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
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(WeatherActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty())
            ActivityCompat.requestPermissions(WeatherActivity.this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), Constant.MULTIPLE_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
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
            mainBinding.tempratureDetails.setText("Temprature:" + Utils.dblValue("" + temp) + " \u2103");
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
