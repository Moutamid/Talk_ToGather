package com.moutamid.talk_togather.Major_Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.databinding.ActivityFaqsactivityBinding;

public class FAQSActivity extends AppCompatActivity {

    private ActivityFaqsactivityBinding b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityFaqsactivityBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FAQSActivity.this , Settings_Activity.class);
                startActivity(intent);
                Animatoo.animateSlideUp(FAQSActivity.this);
                finish();
            }
        });

        b.link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(b.link.getText().toString()));
                startActivity(browserIntent);
            }
        });
    }
}