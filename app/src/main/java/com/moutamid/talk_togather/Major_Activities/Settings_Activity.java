package com.moutamid.talk_togather.Major_Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.moutamid.talk_togather.Initial_Activities.Welcome_Screen;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;

import java.util.Locale;

public class Settings_Activity extends AppCompatActivity {

    ImageView close;
    private Switch pause_notification,mic_status,mode_change;
    private TextView languageTxt,newsTxt,helpTxt,termsTxt,policyTxt,guidelinesTxt,showLoveTxt,faqsTxt,
            contactusTxt,logoutTxt;
    private FirebaseAuth mAuth;
    private SharedPreferencesManager manager;
    private boolean notify;
    private boolean micStatus;
    private boolean theme;
    Spinner spinner;
    String[] listItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new SharedPreferencesManager(this); //get SharedPreferencesManager  instance
        setContentView(R.layout.activity_settings);
        close = findViewById(R.id.close);
        pause_notification = findViewById(R.id.switch1);
        mic_status = findViewById(R.id.switch2);
        mode_change = findViewById(R.id.switch3);
        languageTxt = findViewById(R.id.select_language);
        newsTxt = findViewById(R.id.news);
        helpTxt = findViewById(R.id.help);
        termsTxt = findViewById(R.id.terms);
        policyTxt = findViewById(R.id.policy);
        guidelinesTxt = findViewById(R.id.guideline);
        showLoveTxt = findViewById(R.id.show);
        faqsTxt = findViewById(R.id.faqs);
        contactusTxt = findViewById(R.id.contactus);
        logoutTxt = findViewById(R.id.logout);
        mAuth = FirebaseAuth.getInstance();
        notify = manager.retrieveBoolean("isNotify",false);
        micStatus = manager.retrieveBoolean("isMicEnable",false);
        theme = manager.retrieveBoolean("theme",false);
        spinner = findViewById(R.id.test_spinner);
        String[] tests_name = {};
        getLocale();
        pause_notification.setChecked(notify);
        mic_status.setChecked(micStatus);

        if (theme){
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES);
            mode_change.setChecked(true);
        }else {

            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO);
            mode_change.setChecked(false);
        }

        logoutTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(Settings_Activity.this , Welcome_Screen.class);
                startActivity(intent);
                Animatoo.animateSlideDown(Settings_Activity.this);
                finish();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings_Activity.this , DashBoard.class);
                startActivity(intent);
                Animatoo.animateSlideDown(Settings_Activity.this);
                finish();
            }
        });
        //languageTxt.setText("English");
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this , android.R.layout.simple_spinner_dropdown_item , tests_name);
        //spinner.setAdapter(adapter);

        languageTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLanguageDialogBox();
            }
        });

        faqsTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings_Activity.this , FAQSActivity.class);
                startActivity(intent);
                Animatoo.animateSlideDown(Settings_Activity.this);
                finish();
            }
        });
        pause_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    manager.storeBoolean("isNotify",true);
                }else {
                    manager.storeBoolean("isNotify",false);
                }
            }
        });
        mic_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    manager.storeBoolean("isMicEnable",true);
                }else {
                    manager.storeBoolean("isMicEnable",false);
                }
            }
        });

        mode_change.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_YES);
                    // it will set isDarkModeOn
                    // boolean to false
                    manager.storeBoolean("theme",true);
                }else {

                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_NO);
                    // it will set isDarkModeOn
                    // boolean to false
                    manager.storeBoolean("theme",false);
                }

            }
        });
    }

    private void showLanguageDialogBox() {
        String[] listItems = {"English","French","Spanish"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings_Activity.this);
        builder.setTitle("Choose a Language");
        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){
                    languageTxt.setText("English");
                    setLocale("en","English");
                    recreate();
                }
                else if (i == 1){
                    languageTxt.setText("French");
                    setLocale("fr","French");
                    recreate();
                }else if (i == 2){
                    languageTxt.setText("Spanish");
                    setLocale("es","Spanish");
                    recreate();
                }

                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void getLocale(){

        String lang = manager.retrieveString("lang","");
        String name = manager.retrieveString("lang_name","");
     //   languageTxt.setText(name);
        setLocale(lang,name);
    }

    private void setLocale(String lng,String name) {

        Locale locale = new Locale(lng);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        manager.storeString("lang",lng);
        manager.storeString("lang_name",name);
        if (name.equals("")) {
            languageTxt.setText("English");
        }else {
            languageTxt.setText(name);
        }
    }



    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(Settings_Activity.this , DashBoard.class);
        startActivity(intent);
        Animatoo.animateSlideDown(Settings_Activity.this);
        finish();
    }
}