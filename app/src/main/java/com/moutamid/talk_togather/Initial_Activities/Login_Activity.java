package com.moutamid.talk_togather.Initial_Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.talk_togather.Models.User;
import com.moutamid.talk_togather.Major_Activities.DashBoard;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;

import java.util.Locale;

public class Login_Activity extends AppCompatActivity {

  //  private TextView goto_sighnup;
    private Button login_btn;
    private EditText emailTxt,passwordTxt;
    private Switch rememberSwitch;
    private ProgressDialog pd;
    private DatabaseReference db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ImageView backImg;
    private boolean exists = false;
    private SharedPreferencesManager prefs;
    private boolean theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new SharedPreferencesManager(this); //get SharedPreferencesManager  instance
        theme = prefs.retrieveBoolean("theme",false);//get stored theme, zero is default
        setContentView(R.layout.activity_login);
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
        MobileAds.initialize(getApplicationContext(), getString(R.string.admob_app_id));
        AdView mAdView = (AdView) findViewById(R.id.adView);
        ImageView placeImage = (ImageView) findViewById(R.id.placeholder);
        mAdView.setAdListener(new AdListener() {
            private void showToast(String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoaded() {
                showToast("Ad loaded.");
                if (mAdView.getVisibility() == View.GONE) {
                mAdView.setVisibility(View.VISIBLE);
                placeImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                //  showToast(String.format("Ad failed to load with error code %d.", errorCode));

                mAdView.setVisibility(View.GONE);
                placeImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdOpened() {
                showToast("Ad opened.");
            }

            @Override
            public void onAdClosed() {
                showToast("Ad closed.");
            }

            @Override
            public void onAdLeftApplication() {
                showToast("Ad left application.");
            }
        });

        AdRequest request = new AdRequest.Builder().build();
        mAdView.loadAd(request);

        emailTxt = findViewById(R.id.email);
        passwordTxt = findViewById(R.id.password);
        rememberSwitch = findViewById(R.id.switch1);
        backImg = findViewById(R.id.arrow);
//        goto_sighnup = findViewById(R.id.goto_signup);
    //    goto_sighnup.setAlpha(0f);
      //  goto_sighnup.animate().alpha(1f).setDuration(2000);

        login_btn = findViewById(R.id.login_btn);
        login_btn.setAlpha(0f);
        login_btn.animate().alpha(1f).setDuration(3000);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        pd = new ProgressDialog(Login_Activity.this);
        /*goto_sighnup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goto_sighnup.startAnimation(animation);
                Intent intent = new Intent(Login_Activity.this , SignUp_Activity.class);
                startActivity(intent);
                Animatoo.animateFade(Login_Activity.this);
            }
        });*/
        checkUserExists();
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_btn.startAnimation(animation);
                pd.setMessage("Login....");
                pd.show();
                if (exists){
                    pd.dismiss();
                    Intent intent = new Intent(Login_Activity.this , DashBoard.class);
                    startActivity(intent);
                    Animatoo.animateFade(Login_Activity.this);
                }else {
                    pd.dismiss();
                    Toast.makeText(Login_Activity.this,"Please Sign up first!",Toast.LENGTH_LONG).show();
                }
            }
        });
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login_Activity.this,OTP_Number_Activity.class));
                finish();
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

    private void checkUserExists() {
        // Query query = db.child(user.getUid()).orderByChild("remember").equalTo(true);
        db.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User model = snapshot.getValue(User.class);
                    if (model.isRemember()) {
                        exists = true;
                        emailTxt.setText(model.getEmail());
                        passwordTxt.setText(model.getPassword());
                        rememberSwitch.setChecked(true);
                    }else {
                     checkEmailExists();
                    }
                }else {
                    exists = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkEmailExists() {
        String email = emailTxt.getText().toString();
        String password = passwordTxt.getText().toString();
        db.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                     if (snapshot.hasChild("email")){
                         User models = snapshot.getValue(User.class);
                         if (models.getEmail().equals(email) && models.getPassword().equals(password)){
                             exists = true;
                         }
                     }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}