package com.moutamid.talk_togather.Initial_Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;

import java.util.Locale;

public class Splash extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    private SharedPreferencesManager prefs;
    private boolean theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new SharedPreferencesManager(this); //get SharedPreferencesManager  instance
        theme = prefs.retrieveBoolean("theme",false);//get stored theme, zero is default

        setContentView(R.layout.activity_splash);
        if (theme){
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES);
        }else {

            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO);

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                Intent homeIntent = new Intent(Splash.this, Welcome_Screen.class);
                startActivity(homeIntent);
                Animatoo.animateFade(Splash.this);
                finish();
            }
        },SPLASH_TIME_OUT);
        getLocale();
    }

    private void getLocale(){

        String lang = prefs.retrieveString("lang","");
        setLocale(lang);
    }

    private void setLocale(String lng) {

        Locale locale = new Locale(lng);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        prefs.storeString("lang",lng);
    }

}