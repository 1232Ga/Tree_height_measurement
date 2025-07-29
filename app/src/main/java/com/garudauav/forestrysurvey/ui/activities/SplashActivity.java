package com.garudauav.forestrysurvey.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.garudauav.forestrysurvey.R;
import com.garudauav.forestrysurvey.databinding.ActivitySplashBinding;
import com.garudauav.forestrysurvey.repository.SplashRepository;
import com.garudauav.forestrysurvey.utils.PrefManager;
import com.garudauav.forestrysurvey.viewmodelfactories.SplashViewModelFactory;
import com.garudauav.forestrysurvey.viewmodels.SplashViewModel;

import java.io.IOException;
import java.util.Calendar;

import pl.droidsonroids.gif.GifDrawable;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private SplashViewModel splashViewModel;
    private ActivitySplashBinding binding;
    private PrefManager prefManager;
    private SplashRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefManager = new PrefManager(SplashActivity.this);
        repository = new SplashRepository(getApplication(), prefManager);
        splashViewModel = new ViewModelProvider(SplashActivity.this, new SplashViewModelFactory(prefManager, repository)).get(SplashViewModel.class);

        checkTime();
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.comp_new);
            gifDrawable.setLoopCount(1);
            binding.logogif.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashViewModel.getIsUserLoggedIn().observe(SplashActivity.this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isUserLogin) {
                   if(prefManager.isFirstTimeLaunch()){
                       startActivity(new Intent(SplashActivity.this, OnBoardActivity.class));
                       finish();
                   }else{

                       if (isUserLogin) {
                           startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                           finish();
                       } else {
                           startActivity(new Intent(SplashActivity.this, PreLoginActivity.class));
                           finish();
                       }
                   }
                    }
                });
            }
        }, 6500);

    }

    void checkTime() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        if (currentHour >= 6 && currentHour < 18) {
            // Morning to afternoon period
            if (!prefManager.isAfter6AM()) {
                prefManager.setAfter6AM(true); // Prevents showing the toast again
                prefManager.setAfter6PM(false); // Resets the state for the next period
                splashViewModel.refreshSpeciesList();
            }
        } else {
            // Evening to next morning period
            if (!prefManager.isAfter6PM()) {
                prefManager.setAfter6PM(true);
                prefManager.setAfter6AM(false);
                splashViewModel.refreshSpeciesList();
            }
        }
    }
}