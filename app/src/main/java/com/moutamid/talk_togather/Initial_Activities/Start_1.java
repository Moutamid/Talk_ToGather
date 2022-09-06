package com.moutamid.talk_togather.Initial_Activities;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;

import java.util.Locale;

public class Start_1 extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private SharedPreferencesManager prefs;
    private String mParam2;
    private boolean theme;

    public Start_1() {
        // Required empty public constructor
    }


    public static Start_1 newInstance(String param1, String param2) {
        Start_1 fragment = new Start_1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start_1, container, false);
        prefs = new SharedPreferencesManager(getActivity()); //get SharedPreferencesManager  instance
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

        return view;
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
        getActivity().getResources().updateConfiguration(configuration,getActivity().getResources().getDisplayMetrics());
        prefs.storeString("lang",lng);
    }

}