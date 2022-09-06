package com.moutamid.talk_togather.Initial_Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;

import java.util.HashMap;
import java.util.Locale;

public class SignUp_Activity extends AppCompatActivity {

    Button next_btn;
    private EditText fnameTxt,lnameTxt,emailTxt,passwordTxt,cpassTxt;
    private String email,password,cpassword,fname,lname;
    private Switch rememberSwitch;
    private ProgressDialog pd;
    private DatabaseReference db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ImageView backImg;
    private boolean remember = false;
    private SharedPreferencesManager prefs;
    private boolean theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new SharedPreferencesManager(this);
        theme = prefs.retrieveBoolean("theme",false);//get stored theme, zero is default
        setContentView(R.layout.activity_sign_up);
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
        fnameTxt = findViewById(R.id.fname);
        lnameTxt = findViewById(R.id.lname);
        emailTxt = findViewById(R.id.email);
        passwordTxt = findViewById(R.id.password);
        cpassTxt = findViewById(R.id.cpassword);
        rememberSwitch = findViewById(R.id.switch1);
        backImg = findViewById(R.id.arrow);
        next_btn = findViewById(R.id.sign_up_btn);
        next_btn.setAlpha(0f);
        next_btn.animate().alpha(1f).setDuration(3000);
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        rememberSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    remember = true;
                }else {
                    remember = false;
                }
            }
        });
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validInfo()) {
                    next_btn.startAnimation(animation);
                    pd = new ProgressDialog(SignUp_Activity.this);
                    pd.setMessage("Creating Account....");
                    pd.show();
                    saveData();
                }
            }
        });
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp_Activity.this, Login_Activity.class));
                finish();
            }
        });
    }

    private void saveData() {

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("fname",fname);
        hashMap.put("lname",lname);
        hashMap.put("email",email);
        hashMap.put("password",password);
        hashMap.put("remember",remember);
        db.child(user.getUid()).updateChildren(hashMap);
        pd.dismiss();
        Intent intent = new Intent(SignUp_Activity.this , GetProfile_Pic_Acticity.class);
        startActivity(intent);
        Animatoo.animateFade(SignUp_Activity.this);
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

    public boolean validInfo() {
        fname = fnameTxt.getText().toString();
        lname = lnameTxt.getText().toString();
        email = emailTxt.getText().toString();
        password = passwordTxt.getText().toString();
        cpassword = cpassTxt.getText().toString();

        if(fname.isEmpty()){
            fnameTxt.setText("Input Fullname");
            fnameTxt.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            emailTxt.setError("Input email!");
            emailTxt.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTxt.setError("Please input valid email!");
            emailTxt.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            passwordTxt.setError("Input password!");
            passwordTxt.requestFocus();
            return false;
        }

        if(cpassword.isEmpty()){
            cpassTxt.setText("Input Confirm Password");
            cpassTxt.requestFocus();
            return false;
        }


        return true;
    }

}