package com.garudauav.forestrysurvey.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.garudauav.forestrysurvey.R;
import com.garudauav.forestrysurvey.databinding.ActivityPermissionBinding;
import com.garudauav.forestrysurvey.utils.PrefManager;
import com.google.android.material.snackbar.Snackbar;

public class PermissionActivity extends AppCompatActivity {
    ActivityPermissionBinding binding;
    PrefManager prefManager;
    boolean ischeckedFlag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPermissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefManager=new PrefManager(this);
        binding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              ischeckedFlag=isChecked;
            }
        });

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefManager.setTermsAndConditionsAgreed(ischeckedFlag);
                if(prefManager.isTermsAndConditionsAgreed()){
                    startActivity(new Intent(PermissionActivity.this, PreLoginActivity.class));
                }else{
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Kindly check term and conditions!", Snackbar.LENGTH_LONG);
                    snackbar.show();                }
            }
        });
    }
}