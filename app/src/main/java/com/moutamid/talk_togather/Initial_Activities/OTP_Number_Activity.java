package com.moutamid.talk_togather.Initial_Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;

import java.util.Locale;

public class OTP_Number_Activity extends AppCompatActivity {

    Button start_btn;
    private TextView selectCountryTxt;
    private EditText phoneTxt;
    private CountryCodePicker ccp;
    private SharedPreferencesManager prefs;
    private boolean theme;
    private boolean accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new SharedPreferencesManager(this);
        theme = prefs.retrieveBoolean("theme",false);//get stored theme, zero is default
        setContentView(R.layout.activity_otp_number);
        accountType = getIntent().getBooleanExtra("login",false);
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
        phoneTxt = findViewById(R.id.phone);
        selectCountryTxt = findViewById(R.id.labelCountryTv);
        ccp = findViewById(R.id.countryCodePicker);
        ccp.registerCarrierNumberEditText(phoneTxt);
        start_btn = findViewById(R.id.start_btn);
        start_btn.setAlpha(0f);
        start_btn.animate().alpha(1f).setDuration(3000);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_btn.startAnimation(animation);
                verifyField();
            }
        });
    }

    private void verifyField() {
        String phoneNo  = phoneTxt.getText().toString();
        if (TextUtils.isEmpty(phoneNo)){
            phoneTxt.setText("Please enter your phone number");
            phoneTxt.requestFocus();
        }
        else {
            chechPhoneExists();
        }
    }

    private void chechPhoneExists() {
        String number = ccp.getFullNumberWithPlus();
        Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("phone").equalTo(number);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Intent intent = new Intent(OTP_Number_Activity.this , OTP_Verification_Activity.class);
                    intent.putExtra("phone",number);
                    intent.putExtra("login",accountType);
                    startActivity(intent);
                    Animatoo.animateFade(OTP_Number_Activity.this);
                }else {
                    Intent intent = new Intent(OTP_Number_Activity.this , OTP_Verification_Activity.class);
                    intent.putExtra("phone",number);
                    intent.putExtra("login",accountType);
                    startActivity(intent);
                    Animatoo.animateFade(OTP_Number_Activity.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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