package com.tronicx.tarek.tcalendar;

import android.icu.text.IDNA;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Locale;

public class AddEventActivity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private static final String TIME_PATTERN = "HH:mm";
    ProgressDialog proDlg;
    private TextView lblDate;
    private TextView lblTime;
    private TextView title;
    private TextView description;
    private Button  eventButton;
    private Button addLocationButton;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private String apiname = "addevent";
    private Double Latitude = 0.0;
    private Double Longitude = 0.0;
    private final static int PLACE_PICKER_REQUEST = 1;
    String userid;
    String event_id = "";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        proDlg = new ProgressDialog(this);
        proDlg.setMessage("Connecting...");
        proDlg.setCancelable(false);

        userid = getIntent().getStringExtra("id");

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());

        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        lblDate = (TextView) findViewById(R.id.date);
        lblTime = (TextView) findViewById(R.id.time);
        eventButton = (Button) findViewById(R.id.btn_event);

        AddLocation();

        if(getIntent().getBooleanExtra("editinformation", false))
        {
            setTitle("Event Information");
            title.setText(getIntent().getStringExtra("title"));
            description.setText(getIntent().getStringExtra("description"));
            lblDate.setText(getIntent().getStringExtra("date"));
            lblTime.setText(getIntent().getStringExtra("time"));
            event_id = getIntent().getStringExtra("event_id");
            eventButton.setText("Update Event");
            apiname = "udtevent";
        }
        else
        {
            setTitle("Add Event");
            update();
        }

        eventButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (title.getText().length() == 0)
                {
                    Toast.makeText(AddEventActivity.this, "Input title!", Toast.LENGTH_SHORT).show();
                    return;
                }
                proDlg.show();
                Ion.with(getBaseContext())
                        .load(MainActivity.server_url + apiname)
                        .setBodyParameter("eventname", title.getText().toString())
                        .setBodyParameter("eventdate", lblDate.getText().toString() + " " + lblTime.getText().toString())
                        .setBodyParameter("eventdesc", description.getText().toString())
                        .setBodyParameter("user_id", userid)
                        .setBodyParameter("event_id", event_id)
                        .setBodyParameter("event_latitude", Latitude.toString())
                        .setBodyParameter("event_longitude", Longitude.toString())
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>()
                        {
                            @Override
                            public void onCompleted(Exception e, JsonObject result)
                            {
                                if (e != null){
                                    Toast.makeText(AddEventActivity.this, "Connection Problem!", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(AddEventActivity.this, "Your action is done successfully!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                proDlg.dismiss();
                            }
                        });
            }
        });

        lblTime.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                TimePickerDialog.newInstance(AddEventActivity.this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show(getFragmentManager(), "timePicker");
            }
        });

        lblDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                DatePickerDialog.newInstance(AddEventActivity.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "datePicker");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void update()
    {
        lblDate.setText(dateFormat.format(calendar.getTime()));
        lblTime.setText(timeFormat.format(calendar.getTime()));
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth)
    {
        calendar.set(year, monthOfYear, dayOfMonth);
        update();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute)
    {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        update();
    }

    public void AddLocation()
    {
        addLocationButton = (Button) findViewById(R.id.addLocationButton);
        addLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try
                {
                    Intent intent = builder.build(AddEventActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                }
                catch (GooglePlayServicesRepairableException e)
                {
                    e.printStackTrace();
                }
                catch (GooglePlayServicesNotAvailableException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(AddEventActivity.this, data);
                double latitude = place.getLatLng().latitude;
                double longitude = place.getLatLng().longitude;
                addLocationButton.setText(place.getAddress());
                Latitude = latitude;
                Longitude = longitude;
                Log.d("tag", Latitude.toString() + " & " + Longitude.toString());
            }
        }
    }
}
