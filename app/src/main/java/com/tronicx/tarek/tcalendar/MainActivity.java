package com.tronicx.tarek.tcalendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import java.text.ParseException;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    CaldroidFragment caldroidFragment;
    String userid;
    static JsonArray eventdata;
    static String server_url = "http://www.tronicx.co.uk/calendar/";
    ProgressDialog proDlg;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        proDlg = new ProgressDialog(this);
        proDlg.setMessage("Connecting...");
        proDlg.setCancelable(false);

        userid = getIntent().getStringExtra("id");
        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.cal, caldroidFragment);
        t.commit();

        caldroidFragment.setCaldroidListener(new CaldroidListener() {
                                                 @Override
                                                 public void onSelectDate(Date date, View view) {
                                                     Intent intent = new Intent(MainActivity.this, EventActivity.class);
                                                     intent.putExtra("id", userid);
                                                     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                                     String datetime = dateFormat.format(date);
                                                     intent.putExtra("date", datetime);
                                                     startActivity(intent);
                                                 }
                                             }
        );
        caldroidFragment.refreshView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.addevent, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.addevent:
                Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
                intent.putExtra("id", userid);
                startActivity(intent);
                return true;
        }
        return false;
    }

    public void onStart()
    {
        super.onStart();
        proDlg.show();
        Ion.with(getBaseContext())
                .load(MainActivity.server_url + "getevent")
                .setBodyParameter("user_id", userid)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if (e != null){
                            Toast.makeText(MainActivity.this, "Connection Problem!", Toast.LENGTH_SHORT).show();
                            proDlg.dismiss();
                            return;
                        }
                        eventdata = result;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        caldroidFragment.clearSelectedDates();
                        caldroidFragment.clearBackgroundDrawableForDate(new Date());
                        ColorDrawable blue = new ColorDrawable(getResources().getColor(R.color.red));
                        String today = dateFormat.format(new Date());
                        for (int i = 0; i < eventdata.size(); i ++){
                            JsonObject element = eventdata.get(i).getAsJsonObject();

                            Date date = null;
                            try {
                                date = dateFormat.parse(LoginActivity.JsontoString(element.get("eventdate")));
                                if (LoginActivity.JsontoString(element.get("eventdate")).contains(today)){
                                    caldroidFragment.setBackgroundDrawableForDate(blue, new Date());
                                }
                                caldroidFragment.setSelectedDate(date);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                        caldroidFragment.refreshView();
                        proDlg.dismiss();
                    }
                });
    }
}
