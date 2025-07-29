package com.garudauav.forestrysurvey.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.garudauav.forestrysurvey.BaseClass;
import com.garudauav.forestrysurvey.R;
import com.garudauav.forestrysurvey.adapters.viewpager_adapters.DashBoardPagerAdapter;
import com.garudauav.forestrysurvey.broadcast_receiver.LocationSwitchStateReceiver;
import com.garudauav.forestrysurvey.databinding.ActivityDashboardBinding;
import com.garudauav.forestrysurvey.databinding.AddSpeciesDialogBinding;
import com.garudauav.forestrysurvey.databinding.CodeNotFoundDialogBinding;
import com.garudauav.forestrysurvey.databinding.PermissionDialogBinding;
import com.garudauav.forestrysurvey.db.dao.SpeciesDataDao;
import com.garudauav.forestrysurvey.db.database.ForestryDataBase;
import com.garudauav.forestrysurvey.models.request_models.AddSpeciesValue;
import com.garudauav.forestrysurvey.models.response_models.BaseResponse;
import com.garudauav.forestrysurvey.network.SealedNetworkResult;
import com.garudauav.forestrysurvey.repository.DashBoardRepository;
import com.garudauav.forestrysurvey.repository.SplashRepository;
import com.garudauav.forestrysurvey.ui.fragments.dashboard_module.CustomDataFragment;
import com.garudauav.forestrysurvey.ui.fragments.dashboard_module.TodayDataFragment;
import com.garudauav.forestrysurvey.utils.CommonFunctions;
import com.garudauav.forestrysurvey.utils.PrefManager;
import com.garudauav.forestrysurvey.viewmodelfactories.DashboardViewModelFactory;
import com.garudauav.forestrysurvey.viewmodelfactories.SplashViewModelFactory;
import com.garudauav.forestrysurvey.viewmodels.DashboardViewModel;
import com.garudauav.forestrysurvey.viewmodels.SplashViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardActivity extends BaseClass implements LocationSwitchStateReceiver.LocationStateListener {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1002;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 7;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    ActivityDashboardBinding binding;
    DashboardViewModel viewModel;
    SplashViewModel splashViewModel;
    FusedLocationProviderClient mFusedLocationClient;

    SplashRepository splashRepository;
    AddSpeciesValue speciesValue;
    private LocationSwitchStateReceiver locationSwitchStateReceiver;

    PrefManager prefManager;
    String syncCount = "";
    DashBoardRepository repository;
    AlertDialog alertDialog, alertDialog2;
    ProgressDialog progressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFun();
        DashBoardPagerAdapter adapter = new DashBoardPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TodayDataFragment(), "All Data");
        adapter.addFragment(new CustomDataFragment(), "Custom");
        binding.viewPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);


        if (prefManager.getFragmentVisible().equals("All Data")) {
            binding.viewPager.setCurrentItem(0);
        } else {
            binding.viewPager.setCurrentItem(1);
        }
        viewModel.syncBadgeCount.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                setSyncCountBadge(s);
                syncCount = s;
            }
        });
        getLocation();

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    prefManager.setFragmentVisible("All Data");

                } else {
                    prefManager.setFragmentVisible("Custom");

                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        viewModel.addSpeciesResponse.observe(this, new Observer<SealedNetworkResult<BaseResponse>>() {
            @Override
            public void onChanged(SealedNetworkResult<BaseResponse> result) {
                if (result instanceof SealedNetworkResult.Success) {
                    if (((SealedNetworkResult.Success<BaseResponse>) result).getData().isSuccess()) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                            }
                        }, 1000);
                        Toast.makeText(DashboardActivity.this, "Request generated", Toast.LENGTH_SHORT).show();
                        alertDialog2.dismiss();


                    } else {
                        progressDialog.dismiss();
                        codeNotFoundDialog();

                        //     Toast.makeText(DashboardActivity.this, "USer code not valid", Toast.LENGTH_SHORT).show();

                    }
                } else if (result instanceof SealedNetworkResult.Error) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 1000);
                    Toast.makeText(DashboardActivity.this, ((SealedNetworkResult.Error<BaseResponse>) result).getMessage(), Toast.LENGTH_SHORT).show();

                } else if (result instanceof SealedNetworkResult.Loading) {
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                }
            }
        });

        binding.syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, SyncActivity.class));
            }
        });

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();

            }
        });
        binding.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpMenu(v);
            }
        });
        binding.addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCameraPermissionAllowed = true;
                boolean isFilePermissionAllowed = true;
                boolean isLocationPermissionAllowed = true;
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    isCameraPermissionAllowed = false;

                }
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    isLocationPermissionAllowed = false;
                }


                if (!isCameraPermissionAllowed || !isLocationPermissionAllowed || !isFilePermissionAllowed) {
                    showDialogForPermissions(isCameraPermissionAllowed, isLocationPermissionAllowed, isFilePermissionAllowed);

                } else {
                    if (prefManager.getSpeciesMasterCount() > 0) {
                        startActivity(new Intent(DashboardActivity.this, AddSpeciesDataActivity.class));

                    } else {
                        Toast.makeText(DashboardActivity.this, "Please check internet and restart the app", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });


    }

    private void initFun() {
        checkAndRequestPermissionss();
        prefManager = new PrefManager(this);
        repository = new DashBoardRepository(getApplication(), prefManager);
        viewModel = new ViewModelProvider(this, new DashboardViewModelFactory(prefManager, repository)).get(DashboardViewModel.class);
        progressDialog = new ProgressDialog(this);
        splashRepository = new SplashRepository(getApplication(), prefManager);
        splashViewModel = new ViewModelProvider(DashboardActivity.this, new SplashViewModelFactory(prefManager, splashRepository)).get(SplashViewModel.class);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationSwitchStateReceiver = new LocationSwitchStateReceiver(this);


    }

    private void showPopUpMenu(View v) {

        PopupMenu popup = new PopupMenu(DashboardActivity.this, v, R.style.menu_theme);
        popup.getMenuInflater().inflate(R.menu.dashboard_menu, popup.getMenu());
        if (syncCount.equals("0")) {
            syncCount = "";
        }
        String syncTitle = "Sync Data" + syncCount;
        SpannableString syncTitleSpannable = new SpannableString(syncTitle);
        syncTitleSpannable.setSpan(new SuperscriptSpan(), 9, syncTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        syncTitleSpannable.setSpan(new RelativeSizeSpan(0.75f), 9, syncTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        popup.getMenu().findItem(R.id.sync_btn_menu).setTitle(syncTitleSpannable);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_species_menu:
                        addSpeciesDialog();
                        return true;
                    case R.id.sync_btn_menu:
                        startActivity(new Intent(DashboardActivity.this, SyncActivity.class));

                        return true;
                    case R.id.faq_menu:
                        startActivity(new Intent(DashboardActivity.this, FAQActivity.class));
                        return true;
                    case R.id.logout_btn_menu:
                        logout();

                        return true;
                    default:
                        return false;
                }

            }
        });
        popup.show();
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
        builder.setMessage("Do you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prefManager.clearPreferences();
                        ExecutorService executorService = Executors.newSingleThreadExecutor();

                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                SpeciesDataDao speciesDataDao;
                                ForestryDataBase dataBase = ForestryDataBase.getInstance(getApplicationContext());
                                speciesDataDao = dataBase.speciesDataDao();
                                speciesDataDao.clearSpeciesData();
                            }
                        });
                        startActivity(new Intent(DashboardActivity.this, PreLoginActivity.class));
                        finish();
                        dialog.dismiss();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void addSpeciesDialog() {

        AddSpeciesDialogBinding dialogBinding = AddSpeciesDialogBinding.inflate(getLayoutInflater());

        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this, R.style.MyDialogStyle);
        builder.setView(dialogBinding.getRoot());
        builder.setCancelable(false);

        dialogBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog2.dismiss();
            }
        });
        dialogBinding.okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = dialogBinding.speciesTitle.getText().toString();
                String comment = dialogBinding.speciesComment.getText().toString();
                if (title.isEmpty()) {
                    dialogBinding.speciesTitle.setError("Enter species name");
                } else if (title.length() < 3) {
                    dialogBinding.speciesTitle.setError("Invalid species name");


                } else if (comment.isEmpty()) {
                    dialogBinding.speciesComment.setError("Enter description");
                } else {
                    speciesValue = new AddSpeciesValue(title, comment, CommonFunctions.getRandomColorCode(), prefManager.getUserId(), prefManager.getUserCode());
                    viewModel.addSpeciesValue(speciesValue);

                }
            }
        });

        alertDialog2 = builder.create();
        alertDialog2.show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_species_menu:
                // Handle settings action
                return true;
            case R.id.sync_btn_menu:
                // Handle about action
                return true;
            case R.id.faq_menu:
                // Handle about action
                return true;
            case R.id.logout_btn_menu:
                logout();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void codeNotFoundDialog() {
        CodeNotFoundDialogBinding dialogBinding = CodeNotFoundDialogBinding.inflate(LayoutInflater.from(this));

        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this, R.style.MyDialogStyle);
        builder.setView(dialogBinding.getRoot());

        dialogBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        dialogBinding.clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBinding.userCode.setText("");
            }
        });
        dialogBinding.okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialogBinding.userCode.getText().toString().isEmpty()) {
                    dialogBinding.userCode.setError("Please enter code");

                } else if (dialogBinding.userCode.getText().toString().length() < 8) {
                    dialogBinding.userCode.setError("Please enter valid code");

                } else {
                    String input = dialogBinding.userCode.getText().toString();
                    if (!input.matches("^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9]+$")) {
                        dialogBinding.userCode.setError("Please enter valid code.");
                    } else {
                        prefManager.setUserCode(input);
                        alertDialog.dismiss();
                        speciesValue.setOtpCode(prefManager.getUserCode());
                        viewModel.addSpeciesValue(speciesValue);

                    }


                }
            }
        });

        dialogBinding.userCode.setText(prefManager.getUserCode());


        alertDialog = builder.create();
        alertDialog.show();

    }


    private void showDialogForPermissions(boolean isCameraPermissionAllowed, boolean isLocationPermissionAllowed, boolean isFilePermissionAllowed) {


        PermissionDialogBinding dialogBinding = PermissionDialogBinding.inflate(LayoutInflater.from(DashboardActivity.this));

        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this, R.style.MyDialogStyle);
        builder.setView(dialogBinding.getRoot());

        if (!isCameraPermissionAllowed) {
            dialogBinding.camera.setVisibility(View.VISIBLE);
        }
        if (!isLocationPermissionAllowed) {
            dialogBinding.location.setVisibility(View.VISIBLE);

        }

        dialogBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        dialogBinding.okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppSettings();
                alertDialog.dismiss();
            }
        });


        alertDialog = builder.create();
        alertDialog.show();


    }

    void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            // take persistable Uri Permission for future use
            this.getContentResolver().takePersistableUriPermission(data.getData(), takeFlags);
            SharedPreferences preferences = this.getSharedPreferences("com.example.fileutility", Context.MODE_PRIVATE);
            preferences.edit().putString("filestorageuri", data.getData().toString()).apply();
        } else {
            Log.e("FileUtility", "Some Error Occurred : " + data.getData());
        }
    }


    private void setSyncCountBadge(String syncCountBadge) {

        if (syncCountBadge.equals("0")) {
            binding.syncBadge.setVisibility(View.GONE);


        } else {
            binding.syncBadge.setVisibility(View.VISIBLE);
            binding.syncBadge.setText(syncCountBadge);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }


    private boolean checkAndRequestPermissionss() {
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int wtite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        int fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (wtite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }


        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (coarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(DashboardActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void showLocationSettingsDialog() {
        // Prompt the user to enable location services
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Services")
                .setMessage("Location services are disabled. Do you want to enable them?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Open location settings
                        alertDialog.dismiss();

                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLocation() {
        // Check if location services are enabled
        if (!isLocationEnabled()) {
            // Location services are not enabled, prompt the user to enable them
            showLocationSettingsDialog();
            return;
        }
        // Use FusedLocationProviderClient to get the last known location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (mFusedLocationClient != null) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new com.google.android.gms.tasks.OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                prefManager.setLatitude(latitude + "");
                                prefManager.setLongitude(longitude + "");
                            } else {

                                // Request location updates as fallback
                                LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(10000);

                                LocationCallback locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        if (locationResult == null) {
                                            return;
                                        }
                                        for (Location location : locationResult.getLocations()) {
                                            if (location != null) {

                                                double latitude = location.getLatitude();
                                                double longitude = location.getLongitude();
                                                prefManager.setLatitude(latitude + "");
                                                prefManager.setLongitude(longitude + "");

                                                break;
                                            }
                                        }
                                    }
                                };

                                if (ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DashboardActivity.this
                                        , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                            }
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(LocationManager.MODE_CHANGED_ACTION);
        registerReceiver(locationSwitchStateReceiver, filter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(locationSwitchStateReceiver);
    }

    @Override
    public void onLocationStateChanged(boolean isLocationEnabled) {
        if (isLocationEnabled()) {
            getLocation();
        }
    }

}