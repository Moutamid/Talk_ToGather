package com.moutamid.talk_togather.Initial_Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.moutamid.talk_togather.Major_Activities.DashBoard;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;

import java.util.Locale;

public class Welcome_Screen extends AppCompatActivity {

    Button signUp_btn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private SharedPreferencesManager prefs;
    private boolean theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new SharedPreferencesManager(this);
        theme = prefs.retrieveBoolean("theme",false);//get stored theme, zero is default

        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);

        setContentView(R.layout.activity_welcome_screen);
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
        signUp_btn = findViewById(R.id.signUp_btn);
        signUp_btn.setAlpha(0f);
        signUp_btn.animate().alpha(1f).setDuration(3000);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        signUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp_btn.startAnimation(animation);
                Intent intent = new Intent(Welcome_Screen.this , OTP_Number_Activity.class);
                startActivity(intent);
                finish();
                Animatoo.animateFade(Welcome_Screen.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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


    @Override
    protected void onResume() {
        super.onResume();
        if (user != null){
            Intent intent = new Intent(Welcome_Screen.this , DashBoard.class);
            startActivity(intent);
            finish();
            Animatoo.animateFade(Welcome_Screen.this);
        }
    }
}