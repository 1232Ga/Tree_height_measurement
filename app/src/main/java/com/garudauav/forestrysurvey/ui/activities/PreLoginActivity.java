package com.garudauav.forestrysurvey.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.garudauav.forestrysurvey.databinding.ActivityPreLoginBinding;
import com.garudauav.forestrysurvey.repository.SplashRepository;
import com.garudauav.forestrysurvey.utils.PrefManager;
import com.garudauav.forestrysurvey.viewmodelfactories.SplashViewModelFactory;
import com.garudauav.forestrysurvey.viewmodels.SplashViewModel;

public class PreLoginActivity extends AppCompatActivity {
    private SplashViewModel splashViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPreLoginBinding binding = ActivityPreLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        PrefManager prefManager = new PrefManager(PreLoginActivity.this);
        SplashRepository repository = new SplashRepository(getApplication(), prefManager);
        splashViewModel = new ViewModelProvider(PreLoginActivity.this, new SplashViewModelFactory(prefManager, repository)).get(SplashViewModel.class);

        binding.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PreLoginActivity.this, LoginActivity.class)
                        .putExtra("login_type", "credential"));
            }
        });

        binding.codeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PreLoginActivity.this, LoginActivity.class)
                        .putExtra("login_type", "code"));
            }
        });

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();

    }
}