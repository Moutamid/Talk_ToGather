package com.moutamid.talk_togather.Initial_Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.chaos.view.PinView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class OTP_Verification_Activity extends AppCompatActivity {

    Button start_btn_otp;
    PinView pin;
    ImageView imageView;
    TextView resendCodeTxt;
    String verificationId;
    FirebaseAuth auth;
    ProgressDialog dialog;
    String phonenumber = "";
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    TextView phoneTxt;
    DatabaseReference userDatabase;
    private boolean remember;
    private SharedPreferencesManager prefs;
    private boolean theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new SharedPreferencesManager(this);
        theme = prefs.retrieveBoolean("theme",false);//get stored theme, zero is default
        setContentView(R.layout.activity_otp_verification);

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
        start_btn_otp = findViewById(R.id.start_btn_otp);
        start_btn_otp.setAlpha(0f);
        start_btn_otp.animate().alpha(1f).setDuration(3000);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        phoneTxt = (TextView) findViewById(R.id.phone);
        pin = (PinView) findViewById(R.id.pinview);

        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        phonenumber = getIntent().getStringExtra("phone");
        remember = getIntent().getBooleanExtra("login",false);
        phoneTxt.setText(phonenumber);
        sendVerificationCode(phonenumber);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        imageView = (ImageView) findViewById(R.id.back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent  = new Intent(OTP_Verification_Activity.this,OTP_Number_Activity.class);
                startActivity(intent);
            }
        });
        resendCodeTxt = (TextView)findViewById(R.id.resend_otp);
        resendCodeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeResend(phonenumber);
            }
        });

        start_btn_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_btn_otp.startAnimation(animation);
                String pinCode = pin.getText().toString();
                //  showProgressDialog(pinCode);
                if (TextUtils.isEmpty(pinCode)){
                    Toast.makeText(OTP_Verification_Activity.this,"Please Enter the code",Toast.LENGTH_LONG).show();
                }else {
                    verifyCode(pinCode);
                }

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


    private void setUpVerificationCallbacks() {
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                resendToken = forceResendingToken;
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                if (code != null){
                    pin.setText(code);
                    verifyCode(code);
                }
                signInWithCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                //Toast.makeText(OTP_Verification_Activity.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        };
    }
    private void sendVerificationCode(String phonenumber) {
        //  showProgressDialog(pinCode);
        setUpVerificationCallbacks();

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phonenumber)
                .setTimeout(60L,TimeUnit.SECONDS)
                .setActivity(this).setCallbacks(mCallback).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyCode(String pinCode) {
        PhoneAuthCredential credential  =PhoneAuthProvider.getCredential(verificationId,pinCode);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential phoneAuthCredential) {
        auth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id",user.getUid());
                    hashMap.put("phone",phonenumber);
                    hashMap.put("remember",remember);
                    hashMap.put("status","Offline");
                    userDatabase.child(user.getUid()).updateChildren(hashMap);
                    if (remember){
                        Intent intent = new Intent(OTP_Verification_Activity.this,Login_Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(OTP_Verification_Activity.this,SignUp_Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    Animatoo.animateFade(OTP_Verification_Activity.this);
                }
                else{
//                    Toast.makeText(OTP_Verification_Activity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void codeResend(String phonenumber) {

        setUpVerificationCallbacks();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phonenumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setForceResendingToken(resendToken)
                .setActivity(this).setCallbacks(mCallback).build();

        PhoneAuthProvider.verifyPhoneNumber(options);


        //timeStamp = false;
        //startStop();
    }

}
