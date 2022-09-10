package com.moutamid.talk_togather.Initial_Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.moutamid.talk_togather.Major_Activities.DashBoard;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;

import java.util.Locale;

public class Catagories extends AppCompatActivity {

    ImageView next_btn_img;
    private SharedPreferencesManager prefs;
    private boolean theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new SharedPreferencesManager(this);//get SharedPreferencesManager  instance
        setContentView(R.layout.activity_catagories);
        theme = prefs.retrieveBoolean("theme",false);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
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
        getLocale();
        next_btn_img = findViewById(R.id.next_btn_img);
        next_btn_img.setAlpha(0f);
        next_btn_img.animate().alpha(1f).setDuration(3000);
        next_btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next_btn_img.startAnimation(animation);
                Intent intent = new Intent(Catagories.this , DashBoard.class);
                startActivity(intent);
                Animatoo.animateFade(Catagories.this);
            }
        });
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