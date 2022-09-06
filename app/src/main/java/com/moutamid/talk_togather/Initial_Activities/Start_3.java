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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Start_3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Start_3 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SharedPreferencesManager prefs;
    private boolean theme;

    public Start_3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Start_3.
     */
    // TODO: Rename and change types and number of parameters
    public static Start_3 newInstance(String param1, String param2) {
        Start_3 fragment = new Start_3();
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
        return inflater.inflate(R.layout.fragment_start_3, container, false);

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