package com.moutamid.talk_togather.Major_Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moutamid.talk_togather.Adapters.Adapter_Catagories;
import com.moutamid.talk_togather.Adapters.categoriesListAdapter;
import com.moutamid.talk_togather.Models.Model_Catagories;
import com.moutamid.talk_togather.Models.RoomDetails;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;
import com.moutamid.talk_togather.listener.ItemCheckboxClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Start_Room_Activity extends AppCompatActivity {

    private Button done_btn;
    private ImageView close;
    private TextView text_time_date,selectCategoryTxt,selectCategoryTxt2;
  //  private CheckBox artTxt,businessTxt,educationTxt,foodTxt,moneyTxt,religionTxt,travelTxt;
    private Switch liveEnable;
    private EditText titleTxt,descriptionTxt;
    private RecyclerView categoryList;
    private String title,description,category;
    private String time = "";
    private boolean live = false;
    private SharedPreferencesManager prefs;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private boolean theme;
    private int[] catagories_heading = {
            R.string.category_art,
            R.string.category_business,
            R.string.category_education,
            R.string.category_food,
            R.string.category_money,
            R.string.category_family,
            R.string.category_psychology,
            R.string.category_christianity,
            R.string.category_health,
            R.string.category_leadership,
            R.string.category_sports,
            R.string.category_advice,
            R.string.category_technology,
            R.string.category_beauty,
            R.string.category_culture,
            R.string.category_lifestyle,
            R.string.category_gaming,
            R.string.category_travel,
            R.string.category_social};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new SharedPreferencesManager(this);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        theme = prefs.retrieveBoolean("theme",false);//get stored theme, zero is default
        setContentView(R.layout.activity_start_room);
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
        done_btn = findViewById(R.id.done_btn);
        done_btn.setAlpha(0f);
        done_btn.animate().alpha(1f).setDuration(3000);

        close = findViewById(R.id.close_room);
        text_time_date = findViewById(R.id.text_time_date);
        titleTxt = findViewById(R.id.title);
        descriptionTxt = findViewById(R.id.description);
        selectCategoryTxt = findViewById(R.id.category);
        selectCategoryTxt2 = findViewById(R.id.category2);
        liveEnable = findViewById(R.id.switch1);
        categoryList = findViewById(R.id.list);
        LinearLayoutManager manager = new LinearLayoutManager(Start_Room_Activity.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        categoryList.setLayoutManager(manager);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        selectCategoryTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryList.setVisibility(View.VISIBLE);
                selectCategoryTxt2.setVisibility(View.VISIBLE);
                selectCategoryTxt.setVisibility(View.GONE);
            }
        });

        selectCategoryTxt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryList.setVisibility(View.GONE);
                selectCategoryTxt2.setVisibility(View.GONE);
                selectCategoryTxt.setVisibility(View.VISIBLE);
            }
        });

        getCategoryList();
        liveEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (time.equals("")) {
                    if (b) {
                        live = true;
                    } else {
                        live = false;
                    }
                }else {
                    Toast.makeText(Start_Room_Activity.this,"You have already selected time",Toast.LENGTH_LONG).show();
                }
            }
        });

        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done_btn.startAnimation(animation);
                title = titleTxt.getText().toString();
                description = descriptionTxt.getText().toString();
                saveRoomDetails();
               /* Intent intent = new Intent(Start_Room_Activity.this , Conversation_Activity.class);
                startActivity(intent);
                Animatoo.animateSlideUp(Start_Room_Activity.this);
                finish();*/
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Start_Room_Activity.this , DashBoard.class);
                startActivity(intent);
                Animatoo.animateSlideDown(Start_Room_Activity.this);
                finish();
            }
        });

        text_time_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickDialog();
                //selectDatePickDialig();
            }
        });
    }

    private void selectDatePickDialig() {
        Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(Start_Room_Activity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
             //   String[] mons = new DateFormatSymbols(Locale.ENGLISH).getMonths();
                SimpleDateFormat curFormater = new SimpleDateFormat("dd/mm/yyyy");
                Date selected_date = new Date(year,monthOfYear,dayOfMonth-1);
                time = curFormater.format(selected_date);

                Calendar calendar1 = Calendar.getInstance();
                //   SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
                int y = calendar1.get(Calendar.YEAR);
                int m = calendar1.get(Calendar.MONTH);
                int d = calendar1.get(Calendar.DAY_OF_MONTH);
                int nextdd = calendar1.get(Calendar.DAY_OF_MONTH)+1;

                String current = d + "/" + m + "/" +y;
                String next = nextdd + "/" + m + "/" +y;

                Toast.makeText(Start_Room_Activity.this, time + "\n" + current + "\n" + next, Toast.LENGTH_SHORT).show();
            }
        }, yy, mm, dd);
        datePicker.show();
    }

    private void getCategoryList() {

        categoriesListAdapter adapter_catagories = new categoriesListAdapter(this, catagories_heading);
        categoryList.setAdapter(adapter_catagories);
        adapter_catagories.setItemClickListener(new ItemCheckboxClickListener() {
            @Override
            public void onItemClick(int position, boolean b) {
                if (b){
                    category = getString(catagories_heading[position]);
                }else {
                    category = "";
                }
           //     Toast.makeText(Start_Room_Activity.this,category,Toast.LENGTH_LONG).show();
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

    private void saveRoomDetails() {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Rooms");
        String key = db.push().getKey();
        long timestamp = System.currentTimeMillis();
        if(!time.isEmpty()){
            RoomDetails roomDetails = new RoomDetails(key,user.getUid(),title,description,timestamp,time,category);
            db.child(key).setValue(roomDetails);
            Intent intent = new Intent(Start_Room_Activity.this , DashBoard.class);
            startActivity(intent);
            Animatoo.animateSlideUp(Start_Room_Activity.this);
            finish();
        }else {
            RoomDetails roomDetails = new RoomDetails(key,user.getUid(),title,description,timestamp,category,live);
            db.child(key).setValue(roomDetails);
            Intent intent = new Intent(Start_Room_Activity.this , Conversation_Activity.class);
            intent.putExtra("id",key);
            intent.putExtra("createdId",user.getUid());
            startActivity(intent);
            Animatoo.animateSlideUp(Start_Room_Activity.this);
            finish();
        }
        Toast.makeText(Start_Room_Activity.this,"Room Created!",Toast.LENGTH_LONG).show();
    }

    private void datePickDialog() {
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                int m = monthOfYear + 1;
                String s = dayOfMonth + "/" + m + "/" + year;
                time = s;
                Calendar calendar = Calendar.getInstance();
             //   SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH)+1;
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                int nextdd = calendar.get(Calendar.DAY_OF_MONTH)+1;

                String current = dd + "/" + mm + "/" +yy;
                String next = nextdd + "/" + mm + "/" +yy;
                //Toast.makeText(Start_Room_Activity.this, time + "\n" + current + "\n" + next, Toast.LENGTH_SHORT).show();

                if (time.equals(current)){
                    text_time_date.setText("Today");
                }
                else if (time.equals(next)){
                    text_time_date.setText("Tomorrow");
                }else {
                    text_time_date.setText(time);
                }
            }
        };
        int style = AlertDialog.THEME_HOLO_LIGHT;
        new DatePickerDialog(this, style,date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(Start_Room_Activity.this , DashBoard.class);
        startActivity(intent);
        Animatoo.animateSlideDown(Start_Room_Activity.this);
        finish();
    }
}