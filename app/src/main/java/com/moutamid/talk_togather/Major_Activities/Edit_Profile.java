package com.moutamid.talk_togather.Major_Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.moutamid.talk_togather.Models.User;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Edit_Profile extends AppCompatActivity {

    ImageView close;
    Button update_btn;
    private CircleImageView profile_img;
    private EditText fnameTxt,lnameTxt,bioTxt,phoneTxt,passwordTxt,cpasswordTxt;
    Uri uri;
    private static final int IMAGE_REQUEST = 1;
    private DatabaseReference db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Bitmap bitmap;
    private StorageReference mStorage;
    private String image,fname,lname,phone,bio,pass;
    private SharedPreferencesManager prefs;
    private boolean theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        prefs = new SharedPreferencesManager(this);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);

        theme = prefs.retrieveBoolean("theme",false);//get stored theme, zero is default
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
        profile_img = findViewById(R.id.profile_img);
        fnameTxt = findViewById(R.id.fname);
        lnameTxt = findViewById(R.id.lname);
        bioTxt = findViewById(R.id.bio);
        phoneTxt = findViewById(R.id.phone);
        passwordTxt = findViewById(R.id.password);
        cpasswordTxt = findViewById(R.id.cpassword);
        profile_img.setAlpha(0f);
        profile_img.animate().alpha(1f).setDuration(3000);
        mStorage = FirebaseStorage.getInstance().getReference();
        close = findViewById(R.id.close_edit_profile);
        update_btn = findViewById(R.id.update_btn);
        update_btn.setAlpha(0f);
        update_btn.animate().alpha(1f).setDuration(3000);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        checkUserExists();
        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Edit_Profile.this , Profile_Activity.class);
                startActivity(intent);
                Animatoo.animateSlideDown(Edit_Profile.this);
                finish();
            }
        });
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_btn.startAnimation(animation);
                fname = fnameTxt.getText().toString();
                lname = lnameTxt.getText().toString();
                phone = phoneTxt.getText().toString();
                bio = bioTxt.getText().toString();
                pass = passwordTxt.getText().toString();
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("fname",fname);
                hashMap.put("lname",lname);
                hashMap.put("phone",phone);
                hashMap.put("bio",bio);
                hashMap.put("password",pass);
                hashMap.put("imageUrl",image);
                db.child(user.getUid()).updateChildren(hashMap);
                Toast.makeText(Edit_Profile.this,"Updated Successfully!",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkUserExists() {
        db.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User model =snapshot.getValue(User.class);
                    fname = model.getFname();
                    lname = model.getLname();
                    phone = model.getPhone();
                    bio = model.getBio();
                    pass = model.getPassword();
                    fnameTxt.setText(fname);
                    lnameTxt.setText(lname);
                    phoneTxt.setText(phone);
                    bioTxt.setText(bio);
                    passwordTxt.setText(pass);
                    cpasswordTxt.setText(pass);
                    image = model.getImageUrl();

                    if (model.getImageUrl().equals("")){
                        Picasso.with(getApplicationContext())
                                .load(R.drawable.profile)
                                .into(profile_img);
                    }else {
                        Picasso.with(getApplicationContext())
                                .load(image)
                                .into(profile_img);
                    }
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

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(Edit_Profile.this , Profile_Activity.class);
        startActivity(intent);
        Animatoo.animateSlideDown(Edit_Profile.this);
        finish();
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            uri = data.getData();
            profile_img.setImageURI(uri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                saveInformation();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void saveInformation() {
        ProgressDialog dialog = new ProgressDialog(Edit_Profile.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Uploading your profile....");
        dialog.show();
        if (uri != null) {
            profile_img.setDrawingCacheEnabled(true);
            profile_img.buildDrawingCache();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
            byte[] thumb_byte_data = byteArrayOutputStream.toByteArray();

            final StorageReference reference = mStorage.child("Profile Images").child(System.currentTimeMillis() + ".jpg");
            final UploadTask uploadTask = reference.putBytes(thumb_byte_data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return reference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                image = downloadUri.toString();
                                dialog.dismiss();
                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "Please Select Image ", Toast.LENGTH_LONG).show();

        }
    }
}