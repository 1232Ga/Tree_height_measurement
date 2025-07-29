package com.garudauav.forestrysurvey;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.garudauav.forestrysurvey.broadcast_receiver.NetworkChangeReceiver;
import com.garudauav.forestrysurvey.databinding.OfflineDialogBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseClass extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CODE = 12345;
    public List<String> missingPermission = new ArrayList<>();
    private NetworkChangeReceiver networkChangeReceiver;

    public OfflineDialogBinding offlineDialogBinding;

/*    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA

    };*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receiver to avoid memory leaks
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
    }

   /* protected void checkAndRequestPermissions() {

        // Check for permissions
        for (String eachPermission : REQUIRED_PERMISSION_LIST) {
            if (ContextCompat.checkSelfPermission(this, eachPermission) != PackageManager.PERMISSION_GRANTED) {
                missingPermission.add(eachPermission);
            }
        }

        // Request for missing permissions
        if (missingPermission.isEmpty()) {

        } else {
            ActivityCompat.requestPermissions(this, missingPermission.toArray(new String[missingPermission.size()]),
                    REQUEST_PERMISSION_CODE);
        }
    }*/



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check for granted permission and remove from missing list

        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = grantResults.length - 1; i >= 0; i--) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    missingPermission.remove(permissions[i]);
                }
            }
        }

    }





    public void offLineDialog() {
        offlineDialogBinding = OfflineDialogBinding.inflate(getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(offlineDialogBinding.getRoot());
        AlertDialog alertDialog = builder.create();

        offlineDialogBinding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 alertDialog.dismiss();

            }
        });
        alertDialog.show();

    }

    protected boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check if location services are enabled
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        if (isConnected) {
            Log.d("Network", "Connected");
            return true;
        } else {
            //Nointernet();
            Log.d("Network", "Not Connected");
            return false;
        }
    }

    public boolean validateEmail(EditText username) {
        String emailInput = username.getText().toString().trim();
        if (emailInput.isEmpty()) {
            username.setError("Please enter username");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            username.setError("Please enter a valid email address");
            return false;
        } else {
            username.setError(null);
            return true;
        }
    }
    public static final String PASSWORD_POLICY = "Login failed! please check the email id, password.";
    public boolean isValidPassword(String data, TextInputEditText passwordd, boolean updateUI) {
        String str = data;
        boolean valid = true;

        System.out.println("lhbhjgj" + str);

        if (str.length() < 5) {
            valid = false;
            Toast.makeText(BaseClass.this, PASSWORD_POLICY, Toast.LENGTH_SHORT).show();
        }

        String exp = ".*[0-9].*";
        Pattern pattern = Pattern.compile(exp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        if (!matcher.matches()) {
            valid = false;
            Toast.makeText(BaseClass.this, PASSWORD_POLICY, Toast.LENGTH_LONG).show();
        }

        // Password should contain at least one capital letter
        exp = ".*[A-Z].*";
        pattern = Pattern.compile(exp);
        matcher = pattern.matcher(str);
        if (!matcher.matches()) {
            valid = false;
            // passwordd.setError("");
            Toast.makeText(BaseClass.this, PASSWORD_POLICY, Toast.LENGTH_LONG).show();
        }


        if (updateUI) {
            String error = valid ? null : PASSWORD_POLICY;
            setError(data, error);
        }

        return valid;
    }
    private void setError(Object data, String error) {
        if (data instanceof EditText) {
            if (data instanceof TextInputLayout) {
                ((TextInputLayout) ((EditText) data).getParent().getParent()).setError(error);
            } else {
                ((EditText) data).setError(error);
            }
        }
    }
}
