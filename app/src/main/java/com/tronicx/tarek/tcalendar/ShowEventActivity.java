package com.tronicx.tarek.tcalendar;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ShowEventActivity extends AppCompatActivity {

    //This is a test comment
    //This is a second test
    TextView eventTitle, eventDescription, eventDate, eventTime;
    Button showLocation, editEvent;
    String event_id, user_id, title_str, description_str, date_str, time_str, latitude_str, longitude_str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);
        eventTitle = (TextView) findViewById(R.id.showEventTitle);
        eventDescription = (TextView) findViewById(R.id.showEventDescription);
        eventDate = (TextView) findViewById(R.id.showEventDate);
        eventTime = (TextView) findViewById(R.id.showEventTime);
        setTitle("Event Information");

        title_str = getIntent().getStringExtra("title");
        description_str = getIntent().getStringExtra("description");
        date_str = getIntent().getStringExtra("date");
        time_str = getIntent().getStringExtra("time");
        event_id = getIntent().getStringExtra("event_id");
        user_id = getIntent().getStringExtra("id");
        latitude_str = getIntent().getStringExtra("latitude");
        longitude_str = getIntent().getStringExtra("longitude");

        eventTitle.setText(title_str);
        eventDescription.setText(description_str);
        eventDate.setText(date_str);
        eventTime.setText(time_str);


        EditEventButton();
        ShowLocation();

        if (!IsLocationAvailable(latitude_str, longitude_str))
        {
            showLocation = (Button) findViewById(R.id.showEventShowLocation);
            showLocation.setEnabled(false);
            showLocation.setText("No Location");
            showLocation.setBackgroundColor(Color.GRAY);
        }

        //Debugging
        Log.d("user_id: ", user_id);
        Log.d("LatLong: ", latitude_str + " & " + longitude_str);
    }

    private void EditEventButton()
    {
        editEvent = (Button) findViewById(R.id.showEventEditEventButton);
        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ShowEventActivity.this, AddEventActivity.class);
                intent.putExtra("editinformation", true);
                intent.putExtra("id", user_id);
                intent.putExtra("title", title_str);
                intent.putExtra("event_id", event_id);
                intent.putExtra("description", description_str);
                intent.putExtra("date", date_str);
                intent.putExtra("time", time_str);
                startActivity(intent);
            }
        });
    }

    private void ShowLocation()
    {
        showLocation = (Button)findViewById(R.id.showEventShowLocation);
        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double latitude_double = Double.parseDouble(latitude_str);
                Double longitude_double = Double.parseDouble(longitude_str);
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude_double, longitude_double);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
    }


    private boolean IsLocationAvailable(String latitude, String longitude)
    {
        if (latitude.equals("0") & longitude.equals("0"))
        {
            return false;
        }
        return  true;
    }
}
