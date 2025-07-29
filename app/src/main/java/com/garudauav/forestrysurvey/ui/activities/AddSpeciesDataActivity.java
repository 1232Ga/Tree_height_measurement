package com.garudauav.forestrysurvey.ui.activities;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.garudauav.forestrysurvey.BaseClass;
import com.garudauav.forestrysurvey.R;
import com.garudauav.forestrysurvey.broadcast_receiver.LocationSwitchStateReceiver;
import com.garudauav.forestrysurvey.databinding.ActivityAddSpeciesDataBinding;
import com.garudauav.forestrysurvey.databinding.ImageAnnotationBinding;
import com.garudauav.forestrysurvey.databinding.ImageZoomLayoutBinding;
import com.garudauav.forestrysurvey.models.DistrictMaster;
import com.garudauav.forestrysurvey.models.RFMaster;
import com.garudauav.forestrysurvey.models.SpeciesMaster;
import com.garudauav.forestrysurvey.models.request_models.AddSpeciesValue;
import com.garudauav.forestrysurvey.repository.AddSpeciesDataRepository;
import com.garudauav.forestrysurvey.ui.tree_height_module.CameraActivityHeight;
import com.garudauav.forestrysurvey.utils.MaxValueFilter;
import com.garudauav.forestrysurvey.utils.PrefManager;
import com.garudauav.forestrysurvey.viewmodelfactories.AddSpeciesDataViewModelFactory;
import com.garudauav.forestrysurvey.viewmodels.AddSpeciesDataViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public class AddSpeciesDataActivity extends BaseClass implements LocationSwitchStateReceiver.LocationStateListener {
    private static final int pic_id1 = 123;
    private static final int pic_id2 = 456;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 7;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1002;
    private static final int REQUEST_HEIGHT = 150;
    private FusedLocationProviderClient mFusedLocationClient;
    private AlertDialog alertDialog, alertDialog2;
    private Dialog dialog;
    private AddSpeciesValue speciesValue;
    private ProgressDialog progressDialog;
    private AddSpeciesDataViewModel viewModel;
    private AddSpeciesDataRepository repository;
    private PrefManager prefManager;
    private Context context;
    private LocationSwitchStateReceiver locationSwitchStateReceiver;
    private ActivityAddSpeciesDataBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSpeciesDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = AddSpeciesDataActivity.this;
        repository = new AddSpeciesDataRepository(getApplication());
        prefManager = new PrefManager(AddSpeciesDataActivity.this);
        viewModel = new ViewModelProvider(AddSpeciesDataActivity.this, new AddSpeciesDataViewModelFactory(prefManager, repository)).get(AddSpeciesDataViewModel.class);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        progressDialog = new ProgressDialog(this);
        locationSwitchStateReceiver = new LocationSwitchStateReceiver(this);
        checkAndRequestPermissionss();
//checkAndRequestPermissions();

//rough code for location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
// Request permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddSpeciesDataActivity.this);
                builder.setMessage("Do you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(AddSpeciesDataActivity.this, LoginActivity.class));
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
        });
        viewModel.getPhoto1().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                if (bitmap != null) {
                    binding.image1Lay.setVisibility(View.VISIBLE);
                    binding.imagePlaceholder1.setVisibility(View.GONE);
                    binding.imagePlaceholder2.setVisibility(View.VISIBLE);
                    binding.image1.setImageBitmap(viewModel.getPhoto1().getValue());
                    checkField();
                }
            }
        });
        viewModel.getPhoto2().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                if (bitmap != null) {
                    binding.image2Lay.setVisibility(View.VISIBLE);
                    binding.imagePlaceholder1.setVisibility(View.GONE);
                    binding.imagePlaceholder2.setVisibility(View.GONE);
                    binding.image2.setImageBitmap(viewModel.getPhoto2().getValue());
                    checkField();
                }
            }
        });
        binding.height.setFilters(new InputFilter[]{new MaxValueFilter(999.99)});
// binding.height.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
        binding.girth.setFilters(new InputFilter[]{new MaxValueFilter(999.99)});
// binding.girth.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});

        if (checkAndRequestPermissionss()) {
            getLocation();
        }

        viewModel.getSpeciesNameList().observe(this, new Observer<List<SpeciesMaster>>() {
            @Override
            public void onChanged(List<SpeciesMaster> items) {
                if (items != null) {
                    setspeciesSearch(items);
                }
            }
        });

        viewModel.getDistrictNameList().observe(this, new Observer<List<DistrictMaster>>() {
            @Override
            public void onChanged(List<DistrictMaster> items) {
                if (items != null) {
                    setDistrictNameSpinnerItems(items);
                }
            }
        });

        viewModel.getRFNameList().observe(this, new Observer<List<RFMaster>>() {
            @Override
            public void onChanged(List<RFMaster> items) {
                if (items != null) {
                    setBlockNameSpinnerItems(items);
                }
            }
        });
        binding.detectHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddSpeciesDataActivity.this, CameraActivityHeight.class), REQUEST_HEIGHT);
            }
        });
        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkField()) {
                    try {
                        if (viewModel.insertTreeData()) {
                            showAlert(getString(R.string.data_saved_successfully), "Total no of trees : " + viewModel.getNoOfTree().getValue() + "\n" + "Species Name : " + viewModel.getSpeciesName().getValue());
                        } else {
                            Toast.makeText(AddSpeciesDataActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    } catch (ParseException e) {
                        Toast.makeText(AddSpeciesDataActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                        throw new RuntimeException(e);
                    }
                } else {
                    Toast.makeText(context, "Please fill all details", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.latlng.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkField();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.height.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                if (!input.isEmpty()) {
                    try {
// Prepend "0" if the string starts with a dot to make it parsable
                        if (input.startsWith(".")) {
                            input = "0" + input;
                        }
                        viewModel.setHeight(Float.parseFloat(input));
                    } catch (NumberFormatException e) {
// Handle the case where input is not a valid float
// For safety, you can set height to 0 or a default value
                        viewModel.setHeight(0f);
                    }
                } else {
                    viewModel.setHeight(0f);
                }
                checkField();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.noOfTree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    viewModel.setNoOfTree(Integer.parseInt(s.toString()));
                }
                checkField();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.heightTypeFeet.setBackgroundResource(R.drawable.login_button_back);
        binding.heightTypeInch.setBackgroundResource(R.drawable.login_white_back);
        binding.heightTypeFeet.setTextColor(getResources().getColor(R.color.white));
        binding.heightTypeInch.setTextColor(getResources().getColor(R.color.black));
        binding.heightTypeFeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.heightTypeFeet.setBackgroundResource(R.drawable.login_button_back);
                binding.heightTypeFeet.setTextColor(getResources().getColor(R.color.white));
                binding.heightTypeInch.setBackgroundResource(R.drawable.login_white_back);
                binding.heightTypeInch.setTextColor(getResources().getColor(R.color.black));
                viewModel.setHeightType(3);
            }
        });
        binding.heightTypeInch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.heightTypeInch.setBackgroundResource(R.drawable.login_button_back);
                binding.heightTypeInch.setTextColor(getResources().getColor(R.color.white));
                binding.heightTypeFeet.setTextColor(getResources().getColor(R.color.black));
                binding.heightTypeFeet.setBackgroundResource(R.drawable.login_white_back);
                viewModel.setHeightType(1);
            }
        });

//8-feb-2024
        binding.girthTypeCm.setBackgroundResource(R.drawable.login_button_back);
        binding.girthTypeInch.setBackgroundResource(R.drawable.login_white_back);
        binding.girthTypeCm.setTextColor(getResources().getColor(R.color.white));
        binding.girthTypeInch.setTextColor(getResources().getColor(R.color.black));
        binding.girthTypeCm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.girthTypeCm.setBackgroundResource(R.drawable.login_button_back);
                binding.girthTypeCm.setTextColor(getResources().getColor(R.color.white));
                binding.girthTypeInch.setBackgroundResource(R.drawable.login_white_back);
                binding.girthTypeInch.setTextColor(getResources().getColor(R.color.black));
                viewModel.setGirthType(0);
            }
        });
        binding.girthTypeInch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.girthTypeInch.setBackgroundResource(R.drawable.login_button_back);
                binding.girthTypeInch.setTextColor(getResources().getColor(R.color.white));
                binding.girthTypeCm.setTextColor(getResources().getColor(R.color.black));
                binding.girthTypeCm.setBackgroundResource(R.drawable.login_white_back);
                viewModel.setGirthType(1);
            }
        });

        binding.girth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
// Implement if needed
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                if (!input.isEmpty()) {
                    try {
// Prepend "0" if the string starts with a dot to make it parsable
                        if (input.startsWith(".")) {
                            input = "0" + input;
                        }
                        viewModel.setGirth(Float.parseFloat(input));
                    } catch (NumberFormatException e) {
// Handle the case where input is not a valid float
// For safety, you might want to set girth to 0 or a default value here
                        viewModel.setGirth(0f);
                    }
                } else {
                    viewModel.setGirth(0f);
                }
                checkField();
            }
            @Override
            public void afterTextChanged(Editable s) {
// Implement if needed
            }
        });
        viewModel.getRfName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.rfName.setText(s);
            }
        });
        binding.blockNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setRFName(viewModel.getRFNameList().getValue().get(position).getReservForestMasterName());
                viewModel.blockId = viewModel.getRFNameList().getValue().get(position).getBlockId();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        binding.districtNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setDistrictName(viewModel.getDistrictNameList().getValue().get(position).getDistrictName());
                viewModel.districtId = viewModel.getDistrictNameList().getValue().get(position).getDistrictMasterID();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        binding.imagePlaceholder1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage1();
            }
        });
        binding.imagePlaceholder2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage2();
            }
        });
        binding.enlarge1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showZoomImage(getString(R.string.image1));
            }
        });
        binding.paint1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//show paint options for first image
                showPaint(getString(R.string.image1));
            }
        });
        binding.paint2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//show paint options for second image
                showPaint(getString(R.string.image2));
            }
        });
        binding.enlarge2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showZoomImage(getString(R.string.image2));
            }
        });
        binding.retake1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage1();
            }
        });
        binding.retake2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage2();
            }
        });
        binding.cancelBtn.setOnClickListener(v -> onBackPressed());
        binding.backArrow.setOnClickListener(v -> onBackPressed());
        binding.backArrowImg.setOnClickListener(v -> onBackPressed());
    }
    private void captureImage2() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, pic_id2);
        }
    }

    private void captureImage1() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, pic_id1);
        }
    }

    private void setspeciesSearch(List<SpeciesMaster> arrayList) {
        binding.speciesTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Initialize dialog
                dialog = new Dialog(AddSpeciesDataActivity.this);
// set custom dialog
                dialog.setContentView(R.layout.dialog_searchable_spinner);
// set custom height and width
                dialog.getWindow().setLayout(1000, 800);

// show dialog
                dialog.show();
// Initialize and assign variable
                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.list_view);
// Initialize array adapter
                ArrayAdapter<SpeciesMaster> adapter = new ArrayAdapter<>(AddSpeciesDataActivity.this, android.R.layout.simple_list_item_1, arrayList);
// set adapter
                listView.setAdapter(adapter);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
// when item selected from list
// set selected item on textView
                        binding.speciesTxt.setText(adapter.getItem(position).getSpeciesName());
                        viewModel.setSpeciesName(adapter.getItem(position).getSpeciesName());
                        viewModel.speciesId = adapter.getItem(position).getSpeciesMasterID();
                        checkField();
// Dismiss dialog
                        dialog.dismiss();
                    }
                });
            }
        });
    }
    private void setBlockNameSpinnerItems(List<RFMaster> items) {
        ArrayAdapter<RFMaster> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.blockNameSpinner.setAdapter(adapter);
    }
    private void setDistrictNameSpinnerItems(List<DistrictMaster> items) {
        ArrayAdapter<DistrictMaster> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.districtNameSpinner.setAdapter(adapter);
    }
    private boolean checkField() {
        if (binding.speciesTxt.getText().toString().isEmpty() || binding.latlng.getText().toString().isEmpty()
                || binding.noOfTree.getText().toString().isEmpty()
                || binding.girth.getText().toString().isEmpty()
                || binding.image1.getDrawable() == null
                || binding.image2.getDrawable() == null) {
            binding.saveBtn.setBackgroundColor(ContextCompat.getColor(AddSpeciesDataActivity.this, R.color.not_clickable));
        } else {
            boolean isValidGirth = false;
            try {
                String input = binding.girth.getText().toString();
// Prepend "0" if the string starts with a dot to make it parsable
                if (input.startsWith(".")) {
                    input = "0" + input;
                }
                isValidGirth = Float.parseFloat(input) <= 0.001;
            } catch (NumberFormatException e) {
// Handle the case where input is not a valid float
// For safety, you might want to set girth to 0 or a default value here
                viewModel.setGirth(0f);
            }

            boolean isValidNoOfTree = Integer.parseInt(binding.noOfTree.getText().toString()) < 1;
            if (isValidNoOfTree) {
                binding.noOfTree.setError(getString(R.string.count_should_not_be_less_than_1));
            }
            if (isValidGirth) {
                binding.girth.setError(getString(R.string.count_should_not_be_less_than_0_01));
            }

            if (isValidNoOfTree || isValidGirth) {
                binding.saveBtn.setBackgroundColor(ContextCompat.getColor(AddSpeciesDataActivity.this, R.color.not_clickable));
            } else {
                binding.saveBtn.setBackgroundColor(ContextCompat.getColor(AddSpeciesDataActivity.this, R.color.primary));
                return true;
            }
        }
        return false;
    }

    public void showZoomImage(String image) {
        ImageZoomLayoutBinding dialogBinding = ImageZoomLayoutBinding.inflate(LayoutInflater.from(this));
        AlertDialog.Builder builder = new AlertDialog.Builder(AddSpeciesDataActivity.this);
        builder.setView(dialogBinding.getRoot());
        if (image.equals("image1")) {
            dialogBinding.imageZoom.setImageBitmap(viewModel.getPhoto1().getValue());
        } else {
            dialogBinding.imageZoom.setImageBitmap(viewModel.getPhoto2().getValue());
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    public void showPaint(String image) {
        ImageAnnotationBinding dialogBinding = ImageAnnotationBinding.inflate(LayoutInflater.from(this));
        AlertDialog.Builder builder = new AlertDialog.Builder(AddSpeciesDataActivity.this);
        builder.setView(dialogBinding.getRoot());
        if (image.equals("image1")) {
            dialogBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewModel.setPhoto1(mergeDrawingWithImage(dialogBinding, viewModel.getPhoto1().getValue()));
                    binding.image1.setImageBitmap(viewModel.getPhoto1().getValue());
                    alertDialog.dismiss();
                }
            });
            dialogBinding.treeImg.setImageBitmap(viewModel.getPhoto1().getValue());
        } else {
            dialogBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewModel.setPhoto2(mergeDrawingWithImage(dialogBinding, viewModel.getPhoto2().getValue()));
                    binding.image2.setImageBitmap(viewModel.getPhoto2().getValue());
                    alertDialog.dismiss();
                }
            });

            dialogBinding.treeImg.setImageBitmap(viewModel.getPhoto2().getValue());
        }
        dialogBinding.undoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBinding.paint.undo();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();

    }

    private Bitmap mergeDrawingWithImage(ImageAnnotationBinding dialogBinding, Bitmap photo) {
        int imageViewWidth = dialogBinding.treeImg.getWidth();
        int imageViewHeight = dialogBinding.treeImg.getHeight();
        Bitmap scaledPhoto = Bitmap.createScaledBitmap(photo, imageViewWidth, imageViewHeight, true);
        Bitmap result = Bitmap.createBitmap(imageViewWidth, imageViewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(scaledPhoto, 0, 0, null);
        dialogBinding.paint.draw(canvas);
        return result;
    }

    public void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddSpeciesDataActivity.this);
        builder.setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(AddSpeciesDataActivity.this, DashboardActivity.class));
                        finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean checkAndRequestPermissionss() {
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int wtite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (wtite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (coarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(AddSpeciesDataActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            getLocation();
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == REQUEST_HEIGHT && resultCode == RESULT_OK) {
            float heightOfTree = data.getFloatExtra("height_of_tree", 0);
            binding.height.setText(heightOfTree + "");
            binding.heightTypeFeet.setBackgroundResource(R.drawable.login_button_back);
            binding.heightTypeFeet.setTextColor(getResources().getColor(R.color.white));
            binding.heightTypeInch.setBackgroundResource(R.drawable.login_white_back);
            binding.heightTypeInch.setTextColor(getResources().getColor(R.color.black));
            viewModel.setHeightType(3);
        }
        if (requestCode == pic_id1 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            viewModel.setPhoto1(photo);
            binding.image1Lay.setVisibility(View.VISIBLE);
            binding.imagePlaceholder1.setVisibility(View.GONE);
            binding.imagePlaceholder2.setVisibility(View.VISIBLE);
            binding.image1.setImageBitmap(viewModel.getPhoto1().getValue());
            checkField();
        } else if (requestCode == pic_id2 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            viewModel.setPhoto2(photo);
            binding.image2Lay.setVisibility(View.VISIBLE);
            binding.imagePlaceholder1.setVisibility(View.GONE);
            binding.imagePlaceholder2.setVisibility(View.GONE);
            binding.image2.setImageBitmap(viewModel.getPhoto2().getValue());
            checkField();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage1();
                captureImage2();
            } else {
                Toast.makeText(this, getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }
    protected void showLocationSettingsDialog() {
// Prompt the user to enable location services
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.location_services))
                .setMessage(getString(R.string.location_services_are_disabled_do_you_want_to_enable_them))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
// Open location settings
                        alertDialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void requestLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateLocationInView(location);
// Consider stopping location updates if only a single update is needed
// mFusedLocationClient.removeLocationUpdates(this);
                    break; // Remove this if you want continuous location updates
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
// Permissions are checked at the start of getLocation, should not reach here without permissions
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
    private void getLocation() {
        if (!isLocationEnabled()) {
            showLocationSettingsDialog();
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
// Request missing location permissions.
/*
ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, YOUR_REQUEST_CODE);
*/
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                updateLocationInView(location);
            } else {
                requestLocationUpdates();
            }
        }).addOnFailureListener(this, e -> {
// Handle failure in getting the last known location
            requestLocationUpdates();
        });
        if (binding.latlng.getText().toString().isEmpty()) {
            if (isLocationEnabled() && !progressDialog.isShowing()) {
                progressDialog.setMessage("Fetching location");
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        if (!prefManager.getLongitude().isEmpty() && !prefManager.getLatiude().isEmpty()) {
                            binding.latlng.setText(prefManager.getLatiude() + "," + prefManager.getLongitude());
                            viewModel.setLatitude(Double.parseDouble(prefManager.getLatiude()));
                            viewModel.setLongitude(Double.parseDouble(prefManager.getLongitude()));
                            requestLocationUpdates();
                        }

                    }
                }, 1000);
            }
        } else {
            Log.d("TAG___", "getLocation: ");
        }
        requestLocationUpdates();
    }

    private void updateLocationInView(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        viewModel.setLatitude(latitude);
        viewModel.setLongitude(longitude);
        binding.latlng.setText(String.format(Locale.US, "%f, %f", latitude, longitude));
        prefManager.setLatitude(String.valueOf(latitude));
        prefManager.setLongitude(String.valueOf(longitude));
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