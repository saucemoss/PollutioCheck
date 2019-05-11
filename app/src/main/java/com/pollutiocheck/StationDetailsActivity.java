package com.pollutiocheck;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.pollutiocheck.R;

import static com.pollutiocheck.MainActivity.EXTRA_DETAILS;
import static com.pollutiocheck.MainActivity.EXTRA_NAME;

public class StationDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String Name = extras.getString(EXTRA_NAME);
        int stationId = extras.getInt("EXTRA_STATIONID");
        int bgColorCode = extras.getInt("EXTRA_BGCOLORCODE");
        TextView stationDetailText = findViewById(R.id.StationDetailsTextView);
        stationDetailText.setBackgroundColor(bgColorCode);
        stationDetailText.setText(Name + ", Id stacji:  " + stationId);

    }


}
