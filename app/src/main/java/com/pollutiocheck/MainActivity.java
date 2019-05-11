package com.pollutiocheck;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pollutiocheck.R;

import org.json.JSONException;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements Adapter.onStationClickListener {
    Button getPollutionButton;
    private LocationManager locationManager;
    private ArrayList<StationItem> sl;
    public static String EXTRA_NAME;
    public static String EXTRA_DETAILS;

    static double longitude = 1;
    static double latitude = 1;

    // test coordinates
    //    static double longitude = 18.638306;
    //    static double latitude = 54.372158;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        setContentView(R.layout.activity_main);


        buildRecyclerView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
                return;
            }
        }

        fetchDataProcessStart();

    }

    @SuppressLint("MissingPermission")
    private void fetchDataProcessStart() {
        Toast.makeText(getApplicationContext(), "Getting location...", Toast.LENGTH_SHORT).show();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1000, locationListener);
        if (longitude == 1 && latitude == 1) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = loc.getLongitude();
            latitude = loc.getLatitude();
            Toast.makeText(getApplicationContext(), "Loading data...", Toast.LENGTH_SHORT).show();
            fetchData process = new fetchData();
            process.execute();
        } else {
            Toast.makeText(getApplicationContext(), "Loading data...", Toast.LENGTH_SHORT).show();
            fetchData process = new fetchData();
            process.execute();

        }
    }

    public void buildRecyclerView() {

        fetchData.mRecyclerView = findViewById(R.id.recyclerview);
        fetchData.mRecyclerView.setHasFixedSize(true);
        fetchData.mLayoutManager = new LinearLayoutManager(this);
        fetchData.mAdapter = new Adapter(fetchData.stationList, this);
        fetchData.mRecyclerView.setLayoutManager(fetchData.mLayoutManager);
        fetchData.mRecyclerView.setAdapter(fetchData.mAdapter);

    }


    public void goToActivity2(View view){
        Intent intent = new Intent (this, StationDetailsActivity.class);
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchDataProcessStart();
                }else{

                }
                return;

        }
    }


    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

            longitude = location.getLongitude();
            latitude = location.getLatitude();
            /*
            Toast.makeText(getApplicationContext(), "Location updated, loading data...", Toast.LENGTH_SHORT).show();
            listClear();
            fetchData process = new fetchData();
            process.execute();
            */

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    };

    private void listClear() {
        fetchData.stationList.clear();

        fetchData.mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onStationClick(int position) {
        fetchData.stationList.get(position);
        Intent intent = new Intent(this, StationDetailsActivity.class);

        Bundle extras = new Bundle();
        int bgColorCode = fetchData.stationList.get(position).getColorCode(fetchData.stationList.get(position).getText2());
        extras.putString(EXTRA_NAME, fetchData.stationList.get(position).getText1()+ ("\n") + fetchData.stationList.get(position).getText2()+ ("\n") + fetchData.stationList.get(position).getPollutionValues());
        extras.putInt("EXTRA_BGCOLORCODE", bgColorCode);
        extras.putInt("EXTRA_STATIONID", fetchData.stationList.get(position).getAppId());

        intent.putExtras(extras);
        startActivity(intent);

    }
}
