package com.garudauav.forestrysurvey.ui.tree_height_module;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.garudauav.forestrysurvey.R;
import com.garudauav.forestrysurvey.databinding.AccurateHeightDialogBinding;
import com.garudauav.forestrysurvey.databinding.ActivityCameraHeightBinding;
import com.garudauav.forestrysurvey.databinding.EnterHeightPhoneBinding;
import com.garudauav.forestrysurvey.databinding.WrongHeightDialogBinding;
import com.garudauav.forestrysurvey.utils.AutoFitTextureView;
import com.garudauav.forestrysurvey.utils.PrefManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class CameraActivityHeight extends AppCompatActivity {
    private  final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    private SensorManager mSensorManager;
    private MySensorEventListener mSensorEventListener;

    private String cameraID;
    private Size imageDimension;
    private CameraDevice cameraDevice;
    private CaptureRequest.Builder captureRequestBuilder;
    private CameraCaptureSession myCameraCaptureSession;
    private HandlerThread cameraThread;
    private Handler cameraHandler;
    private CameraCharacteristics myCameraCharacteristics;
    int desclaimerCounter = 0;
    private TextView textView2;
    private AutoFitTextureView cameraOutputView;
    private  EditText heightBox;
    private  Button markTopBtn, markBottomBtn;

    private float angle;
    private  float angle2;
    private ImageView crosshair;
    private float userHeight = 164.592f;//in cm
    private float heightOfTree = -1;
    private AlertDialog alertDialog;
    private  ActivityCameraHeightBinding binding;
    private String floatRegex = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";

    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraHeightBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        prefManager = new PrefManager(this);
        crosshair = findViewById(R.id.crosshair);
        crosshair.setImageDrawable(getDrawable(R.drawable.ic_cross_hair));

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorEventListener = new MySensorEventListener(mSensorManager);

        cameraOutputView = findViewById(R.id.texture);
        cameraOutputView.setSurfaceTextureListener(textureListener);

        heightBox = findViewById(R.id.height);
        textView2 = findViewById(R.id.text2);
        markBottomBtn = findViewById(R.id.bottom_mark_btn);
        markTopBtn = findViewById(R.id.mark_top_btn);
        binding.desclaimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDisclaimerPopup(false);
            }
        });

        binding.heightPhoneLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipbtn(true);
               // showEnterHeightDialog();
            }
        });
      //  prefManager.setHeightScreenFirstTime(true);
        if (prefManager.isHeightFirstTime()) {
            showDisclaimerPopup(true);
            prefManager.setHeightScreenFirstTime(false);
        } else {
            showEnterHeightDialog();
        }
        //
    }



    private void showDisclaimerPopup(boolean isFirstTime) {
        binding.crosshair.setVisibility(View.GONE);
        binding.markTopBtn.setVisibility(View.GONE);
        binding.bottomMarkBtn.setVisibility(View.GONE);
        desclaimerCounter = 0;
        int[] deslaimerImgList = {R.drawable.instruction_height_1,R.drawable.disclaimer_5, R.drawable.instruction_height_2, R.drawable.instruction_height_3, R.drawable.instruction_height_4};
        int[] disclaimerTxtList = {R.string.desclaimer_1txt, R.string.desclaimer_5txt,R.string.desclaimer_2txt, R.string.desclaimer_3txt, R.string.desclaimer_4txt};
        binding.declaimerCard.setVisibility(View.VISIBLE);
        binding.desclaimerBtn.setEnabled(false);
        binding.nextBtnTxt.setText("Next");
        binding.skipBtn.setVisibility(View.VISIBLE);
        binding.desclaimerImg.setImageDrawable(getDrawable(deslaimerImgList[desclaimerCounter]));
        binding.desclaimerTxt.setText(disclaimerTxtList[desclaimerCounter]);
        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(desclaimerCounter<3) {

                    desclaimerCounter++;
                    binding.desclaimerImg.setImageDrawable(getDrawable(deslaimerImgList[desclaimerCounter]));
                    binding.desclaimerTxt.setText(disclaimerTxtList[desclaimerCounter]);

                }else
                if(desclaimerCounter==3){
                    desclaimerCounter++;
                    binding.desclaimerImg.setImageDrawable(getDrawable(deslaimerImgList[desclaimerCounter]));
                    binding.desclaimerTxt.setText(disclaimerTxtList[desclaimerCounter]);
                    binding.skipBtn.setVisibility(View.GONE);
                    binding.nextBtnTxt.setText("Done");
                } else {
                    showEnterHeightDialog();
                    desclaimerCounter = 0;
                    binding.declaimerCard.setVisibility(View.GONE);
                    binding.desclaimerBtn.setEnabled(true);
                    binding.crosshair.setVisibility(View.VISIBLE);
                    //binding.markTopBtn.setVisibility(View.VISIBLE);
                   // binding.bottomMarkBtn.setVisibility(View.VISIBLE);

                }

            }
        });

        binding.skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipbtn(isFirstTime);
            }
        });

    }

    private void skipbtn(boolean isFirstTime) {
        binding.desclaimerBtn.setEnabled(true);
        desclaimerCounter = 0;
        binding.declaimerCard.setVisibility(View.GONE);
        binding.crosshair.setVisibility(View.VISIBLE);

        binding.bottomMarkBtn.setVisibility(View.VISIBLE);
        if(isFirstTime){
            showEnterHeightDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.

        if (cameraOutputView.getSurfaceTexture() != null) {
            openCamera();
        }
        heightBox.setText(meterToFeet((float) userHeight / 100) + "");

        startCameraHandlerThread();

        mSensorManager.registerListener(mSensorEventListener
                , mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);

        mSensorManager.registerListener(mSensorEventListener
                , mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void onPause() {
        super.onPause();

        myCameraCaptureSession.close();
        closeCamera();
        stopCameraHandlerThread();
        // Don't receive any more updates from either sensor.
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    public void onButtonClick(View view) {
        mSensorEventListener.updateOrientationAngles();
        if (userHeight > 0) {
            if (view.getId() == R.id.bottom_mark_btn) {

                markBottomBtn.setText("");
                binding.bottomProgress.setVisibility(View.VISIBLE);
                markBottomBtn.setEnabled(false);
                // Handler to add delay
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // After 1 sec, hide progress bar, show original text, and enable button
                        binding.bottomProgress.setVisibility(View.GONE);
                        markBottomBtn.setText("Mark\nBottom");
                        markBottomBtn.setEnabled(true);
                        markBottomBtn.setVisibility(View.GONE);
                        markTopBtn.setVisibility(View.VISIBLE);
                    }
                }, 1000);



                angle = Math.abs(mSensorEventListener.getPitch());
                binding.image1.setImageDrawable(getDrawable(R.drawable.ic_green_tick_circle));
                binding.image2.setImageDrawable(getDrawable(R.drawable.ic_green_tick_circle));
                binding.image3.setImageDrawable(getDrawable(R.drawable.ic_3_white));
            } else if (view.getId() == R.id.mark_top_btn) {
              try {
                  angle2 = Math.abs(mSensorEventListener.getPitch());
                  float quadrant = mSensorEventListener.getPitchQuadrantUpDown();
                  angle2 = angle2 * (Math.signum(quadrant));

                  userHeight = feetToCm(Float.valueOf(heightBox.getText().toString())) / 100f;
                  float length = (float) (userHeight * Math.tan(angle));
                  float angleCalc = (float) (Math.PI / 2.0 - Math.abs(angle2));
                  float dist = (float) (length * Math.tan(angleCalc));

                  float finalDisp = dist * (-1) / Math.signum(angle2);
                  Log.d("String_checkk__", String.valueOf(userHeight + finalDisp));
                  BigDecimal meter = new BigDecimal(String.valueOf(userHeight + finalDisp));
                  // Set the scale to 2 for two decimal places, and use HALF_UP rounding mode.

                  heightOfTree = meter.setScale(2, RoundingMode.HALF_UP).floatValue();//meterToFeet(userHeight + finalDisp);
                  markTopBtn.setEnabled(false);
                  binding.crosshair.setVisibility(View.GONE);
                  binding.centerProgress.setVisibility(View.VISIBLE);
                  binding.bottomMarkBtn.setVisibility(View.GONE);
                  binding.markTopBtn.setVisibility(View.GONE);
                  new Handler().postDelayed(new Runnable() {
                      @Override
                      public void run() {
                          binding.crosshair.setVisibility(View.VISIBLE);
                          binding.centerProgress.setVisibility(View.GONE);
                          if (heightOfTree >= 0.01) {
                              isHeightAccurate();
                              textView2.setText(" " + heightOfTree + " meter");
                          } else {
                              showWrongHeightDialog();
                          }
                          markTopBtn.setEnabled(true);
                      }
                  },1000);

              }catch (Exception e){
                  Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
              }

            }
        } else {
            Toast.makeText(this, "Please specify the height at which you are holding your device!", Toast.LENGTH_SHORT).show();
        }


    }


    public float feetToCm(float feet) {
        // Check for valid, positive numbers and avoid NaN or infinite values
        if (feet > 0 && !Float.isNaN(feet) && !Float.isInfinite(feet)) {
            try {
                // Use String.valueOf() to convert the result to String first to avoid precision issues
                BigDecimal cm = new BigDecimal(String.valueOf(feet * 30.48));
                // Set the scale to 2 for two decimal places, and use HALF_UP rounding mode.
                return cm.setScale(2, RoundingMode.HALF_UP).floatValue();
            } catch (NumberFormatException e) {
                // Log the exception or handle it as necessary
                System.err.println("Invalid input for conversion: " + feet);
                return 0; // or handle error appropriately
            }
        } else {
            return 0;
        }
    }

    public static float meterToFeet(float meters) {
        // Check for valid, positive numbers greater than zero
        if (!Float.isNaN(meters) && !Float.isInfinite(meters)) {
            try {
                BigDecimal feet = new BigDecimal(String.valueOf(meters * 3.28084));
                // Set the scale to 2 for two decimal places, and use HALF_UP rounding mode.
                return feet.setScale(2, RoundingMode.HALF_UP).floatValue();
            } catch (NumberFormatException e) {
                // Log the exception or handle it as necessary
                System.err.println("Invalid input for conversion: " + meters);
                return 0; // or handle error appropriately
            }
        } else {
            return 0;
        }
    }

    private void startCameraHandlerThread() {
        cameraThread = new HandlerThread("Camera Background");
        cameraThread.start();
        cameraHandler = new Handler(cameraThread.getLooper());
    }

    private void stopCameraHandlerThread() {
        cameraThread.quitSafely();
        cameraThread = null;
        cameraHandler = null;
    }

    private void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    private void openCamera() {
        if (cameraDevice == null) {
            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                cameraID = manager.getCameraIdList()[0];
                myCameraCharacteristics = manager.getCameraCharacteristics(cameraID);
                StreamConfigurationMap map = myCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];

                //   cameraOutputView.setAspectRatio(imageDimension.getHeight(),imageDimension.getWidth());
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }

                manager.openCamera(cameraID, cameraStateCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    protected void createCameraPreview() {
        try {

            SurfaceTexture texture = cameraOutputView.getSurfaceTexture();
            assert texture != null;

            texture.setDefaultBufferSize(imageDimension.getHeight(), imageDimension.getWidth());
            Surface surface = new Surface(texture);


            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            myCameraCaptureSession = cameraCaptureSession;
                            updatePreview();
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                            Toast.makeText(getApplicationContext(), "on configure failed", Toast.LENGTH_SHORT).show();

                        }
                    }
                    , null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void updatePreview() {
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            myCameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    private final CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    void isHeightAccurate() {

        binding.image1.setImageDrawable(getDrawable(R.drawable.ic_green_tick_circle));
        binding.image2.setImageDrawable(getDrawable(R.drawable.ic_green_tick_circle));
        binding.image3.setImageDrawable(getDrawable(R.drawable.ic_green_tick_circle));
        AccurateHeightDialogBinding dialogBinding = AccurateHeightDialogBinding.inflate(LayoutInflater.from(CameraActivityHeight.this));
        AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivityHeight.this, R.style.MyDialogStyle);
        builder.setView(dialogBinding.getRoot());
        builder.setCancelable(false);
        dialogBinding.treeHeight.setText(heightOfTree + "");
        dialogBinding.okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("height_of_tree", heightOfTree);
                setResult(RESULT_OK, returnIntent);
                finish();

            }
        });
        dialogBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.bottomMarkBtn.setVisibility(View.VISIBLE);
                binding.markTopBtn.setVisibility(View.GONE);
                alertDialog.dismiss();
                // showEnterHeightDialog();
                binding.image1.setImageDrawable(getDrawable(R.drawable.ic_green_tick_circle));
                binding.image2.setImageDrawable(getDrawable(R.drawable.ic_2_white));
                binding.image3.setImageDrawable(getDrawable(R.drawable.ic_3_grey));


            }
        });
        alertDialog = builder.create();
        alertDialog.show();

    }

    void showEnterHeightDialog() {
        binding.image1.setImageDrawable(getDrawable(R.drawable.ic_1_white));
        binding.image2.setImageDrawable(getDrawable(R.drawable.ic_2_grey));
        binding.image3.setImageDrawable(getDrawable(R.drawable.ic_3_grey));
        EnterHeightPhoneBinding dialogBinding = EnterHeightPhoneBinding.inflate(LayoutInflater.from(this));
        AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivityHeight.this, R.style.MyDialogStyle);
        builder.setView(dialogBinding.getRoot());
        builder.setCancelable(false);
        dialogBinding.okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //enter height
                String stringHeight = dialogBinding.height.getText().toString();
                if (dialogBinding.height.getText().toString().isEmpty()) {
                    dialogBinding.height.setError("Enter height");


                } else if (!stringHeight.isEmpty() && stringHeight.matches(floatRegex) && Float.parseFloat(stringHeight) >= 1 && Float.parseFloat(stringHeight) <= 10) {

                    binding.heightOfPhone.setText(dialogBinding.height.getText().toString());
                    userHeight = feetToCm(Float.parseFloat(dialogBinding.height.getText().toString())) / 100f;
                    alertDialog.dismiss();
                    crosshair.setImageDrawable(getDrawable(R.drawable.ic_cross_hair));
                    binding.image1.setImageDrawable(getDrawable(R.drawable.ic_green_tick_circle));
                    binding.image2.setImageDrawable(getDrawable(R.drawable.ic_2_white));
                    binding.image3.setImageDrawable(getDrawable(R.drawable.ic_3_grey));
                    binding.bottomMarkBtn.setVisibility(View.VISIBLE);
                    binding.heightPhoneLay.setVisibility(View.VISIBLE);
                    binding.markTopBtn.setVisibility(View.GONE);

                } else
                    dialogBinding.height.setError("Enter valid height");

            }
        });

        dialogBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //back to add tree screen
                alertDialog.dismiss();
                finish();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
        binding.bottomMarkBtn.setVisibility(View.GONE);
        binding.markTopBtn.setVisibility(View.GONE);
        binding.heightPhoneLay.setVisibility(View.GONE);
    }

    private void showWrongHeightDialog() {
        binding.image1.setImageDrawable(getDrawable(R.drawable.ic_green_tick_circle));
        binding.image2.setImageDrawable(getDrawable(R.drawable.ic_green_tick_circle));
        binding.image3.setImageDrawable(getDrawable(R.drawable.ic_green_tick_circle));
        WrongHeightDialogBinding dialogBinding = WrongHeightDialogBinding.inflate(LayoutInflater.from(CameraActivityHeight.this));
        AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivityHeight.this, R.style.MyDialogStyle);
        builder.setView(dialogBinding.getRoot());
        builder.setCancelable(false);
        dialogBinding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bottomMarkBtn.setVisibility(View.VISIBLE);
                binding.markTopBtn.setVisibility(View.GONE);
                alertDialog.dismiss();
                // showEnterHeightDialog();
                binding.image1.setImageDrawable(getDrawable(R.drawable.ic_green_tick_circle));
                binding.image2.setImageDrawable(getDrawable(R.drawable.ic_2_white));
                binding.image3.setImageDrawable(getDrawable(R.drawable.ic_3_grey));
            }
        });
        dialogBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent returnIntent = new Intent();
                returnIntent.putExtra("height_of_tree", heightOfTree);
                setResult(RESULT_OK, returnIntent);*/
                finish();


            }
        });
        alertDialog = builder.create();
        alertDialog.show();
        binding.bottomMarkBtn.setVisibility(View.GONE);
        binding.markTopBtn.setVisibility(View.GONE);
    }
}