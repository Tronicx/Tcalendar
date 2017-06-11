package com.tronicx.tarek.tcalendar;


import android.app.ProgressDialog;
import android.app.usage.UsageEvents;
import android.content.DialogInterface;
import android.content.Intent;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventActivity extends AppCompatActivity
{
    String date;
    ListView itemList;
    List<String> event_id_list;
    List<String> event_desc_list;
    ProgressDialog proDlg;
    String user_id, latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        date = getIntent().getStringExtra("date");
        user_id = getIntent().getStringExtra("id");

        proDlg = new ProgressDialog(this);
        proDlg.setMessage("Connecting...");
        proDlg.setCancelable(false);

        setTitle(date + " Event List");

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        event_id_list = new ArrayList<String>();
        event_desc_list = new ArrayList<String>();

        for (int i = 0; i < MainActivity.eventdata.size(); i ++)
        {
            JsonObject element = MainActivity.eventdata.get(i).getAsJsonObject();
            if (LoginActivity.JsontoString(element.get("eventdate")).contains(date))
            {
                Map<String, String> datum = new HashMap<String, String>(2);
                datum.put("title", LoginActivity.JsontoString(element.get("eventname")));
                datum.put("date", LoginActivity.JsontoString(element.get("eventdate")));
                event_id_list.add(LoginActivity.JsontoString(element.get("event_id")));
                event_desc_list.add(LoginActivity.JsontoString(element.get("eventdesc")));
                datum.put("latitude", LoginActivity.JsontoString(element.get("event_latitude")));
                datum.put("longitude", LoginActivity.JsontoString(element.get("event_longitude")));
                data.add(datum);
            }
        }

        final SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[] {"title", "date"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});
        itemList =(ListView) findViewById(R.id.eventlist);
        itemList.setAdapter(adapter);

        itemList.setOnItemClickListener(new ListView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id)
            {
                Intent intent = new Intent(EventActivity.this, ShowEventActivity.class);
                intent.putExtra("editinformation", true);
                intent.putExtra("id", user_id);
                intent.putExtra("title", data.get(i).get("title"));
                intent.putExtra("event_id", event_id_list.get(i));
                intent.putExtra("description", event_desc_list.get(i));
                intent.putExtra("date", date);
                intent.putExtra("time", data.get(i).get("date").substring(11, 16));
                intent.putExtra("latitude", data.get(i).get("latitude"));
                intent.putExtra("longitude", data.get(i).get("longitude"));
                startActivity(intent);
                finish();
            }
        });

        itemList.setOnItemLongClickListener(new ListView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l)
            {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        switch (which)
                        {
                            case DialogInterface.BUTTON_POSITIVE:
                                proDlg.show();
                                Ion.with(getBaseContext())
                                        .load(MainActivity.server_url + "delevent")
                                        .setBodyParameter("event_id", event_id_list.get(i))
                                        .asJsonObject()
                                        .setCallback(new FutureCallback<JsonObject>()
                                        {
                                            @Override
                                            public void onCompleted(Exception e, JsonObject result)
                                            {
                                                if (e != null)
                                                {
                                                    Toast.makeText(EventActivity.this, "Connection Problem!", Toast.LENGTH_SHORT).show();
                                                    proDlg.dismiss();
                                                    return;
                                                }
                                                data.remove(i);
                                                event_id_list.remove(i);
                                                event_desc_list.remove(i);
                                                adapter.notifyDataSetChanged();
                                                proDlg.dismiss();
                                            }
                                        });
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this);
                builder.setMessage("Do you want to delete this event?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                return true;
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

}
