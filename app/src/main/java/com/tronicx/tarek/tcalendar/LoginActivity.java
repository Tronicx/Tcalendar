package com.tronicx.tarek.tcalendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
//import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class LoginActivity extends AppCompatActivity
{

    ProgressDialog proDlg;
    public static String JsontoString(JsonElement element)
    {
        String string = element.toString();
        return string.substring(1, string.length() - 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.title_activity_login);
        CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ALL ) );
        proDlg = new ProgressDialog(this);
        proDlg.setMessage("Connecting...");
        proDlg.setCancelable(false);
    }

    public void onLogin(View v)
    {
        EditText name = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        proDlg.show();

        Ion.with(getBaseContext())
                .load(MainActivity.server_url + "login")
                .setBodyParameter("username", name.getText().toString())
                .setBodyParameter("password", password.getText().toString())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>()
                {
                    @Override
                    public void onCompleted(Exception e, JsonObject result)
                    {
                        if (e != null){
                            Toast.makeText(LoginActivity.this, "Connection Problem!", Toast.LENGTH_SHORT).show();
                            proDlg.dismiss();
                            return;
                        }
                        String resultTxt = LoginActivity.JsontoString(result.get("result"));
                        if (resultTxt.equals("success"))
                        {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("id", LoginActivity.JsontoString(result.get("user_id")));
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, result.get("message").toString(), Toast.LENGTH_SHORT).show();
                        }
                        proDlg.dismiss();
                    }
                });
    }

    public void onSignup(View v)
    {
        EditText name = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        proDlg.show();
        Ion.with(getBaseContext())
                .load(MainActivity.server_url + "register")
                .setBodyParameter("username", name.getText().toString())
                .setBodyParameter("password", password.getText().toString())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>()
                {
                    @Override
                    public void onCompleted(Exception e, JsonObject result)
                    {
                        if (e != null)
                        {
                            Toast.makeText(LoginActivity.this, "Connection Problem!", Toast.LENGTH_SHORT).show();
                            proDlg.dismiss();
                            return;
                        }
                        String resultTxt = LoginActivity.JsontoString(result.get("result"));
                        if (resultTxt.equals("success"))
                        {
                            Toast.makeText(LoginActivity.this, result.get("message").toString(), Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, result.get("message").toString(), Toast.LENGTH_SHORT).show();
                        }
                        proDlg.dismiss();
                    }
                });
    }
}
