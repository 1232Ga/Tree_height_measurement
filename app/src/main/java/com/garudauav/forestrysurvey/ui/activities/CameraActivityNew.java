package com.garudauav.forestrysurvey.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.garudauav.forestrysurvey.R;

import java.io.IOException;

public class CameraActivityNew extends AppCompatActivity  implements SurfaceHolder.Callback, View.OnClickListener {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private ImageView captureImage;
    private int cameraId;
    private int rotation;
    String eng_img_array;
    SharedPreferences sharedpref_eng_imgarray;
    private boolean safeToTakePicture = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // camera surface view created
        cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        captureImage = (ImageView) findViewById(R.id.captureImage);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        captureImage.setOnClickListener(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sharedpref_eng_imgarray = this.getSharedPreferences("sharedPref_Engimg_array", Context.MODE_PRIVATE);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (openCamera(Camera.CameraInfo.CAMERA_FACING_BACK)) {
            alertCameraDialog();
        }

    }

    private boolean openCamera(int id) {
        boolean result = false;
        System.out.println("id" + id);
        cameraId = id;
        releaseCamera();

        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (camera != null) {
            try {
                setUpCamera(camera);
                camera.setErrorCallback((error, camera) -> {

                });
                camera.setPreviewDisplay(surfaceHolder);

                Camera.Parameters p = camera.getParameters();
                p.set("jpeg-quality", 70);
                p.setPictureFormat(PixelFormat.JPEG);
                p.setPictureSize(640, 480);
                camera.setParameters(p);

                camera.startPreview();
                Camera.Parameters parameters = camera.getParameters();
                if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    camera.setParameters(parameters);
                }
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            // Autofocus succeeded, now ready to capture an image
                        }
                    }
                });

                result = true;
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
                releaseCamera();
            }
        }
        return !result;
    }

    private void setUpCamera(Camera c) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        rotation = getWindowManager().getDefaultDisplay().getRotation();

        int degree = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;
            default:
                break;
        }

//        Log.i("mobi", degree+" orientation: "+info.orientation);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // frontFacing
            rotation = (info.orientation + degree) % 360;
            rotation = (360 - rotation) % 360;
//            Log.i("mobi", "rotation1: "+rotation);
        } else {
            // Back-facing
            rotation = (info.orientation - degree + 360) % 360;
//            Log.i("mobi", "rotation2: "+rotation);
        }
        c.setDisplayOrientation(rotation);

    }

    private void releaseCamera() {
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.setErrorCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.captureImage:
                takeImage();
                break;

            default:
                break;
        }
    }

    private void takeImage() {

        Log.i("mobi", "takeImage: 1");

        if (!safeToTakePicture)
            return;

        safeToTakePicture = false;

        Log.i("mobi", "takeImage: 2");
        camera.takePicture(null, null, (data, camera) -> {
            try {

                eng_img_array = Base64.encodeToString(data, Base64.DEFAULT);

                SharedPreferences.Editor editor = sharedpref_eng_imgarray.edit();
                editor.putString("Image_array_eng", eng_img_array);
                editor.putInt("rotation", rotation);
                editor.putInt("cameraId", cameraId);
                editor.apply();

                releaseCamera();

                Intent intent = new Intent();
                setResult(456, intent);
                finish();

            } catch (Exception e) {
                safeToTakePicture = true;
                e.printStackTrace();
            }

        });

    }

    private void alertCameraDialog() {
        AlertDialog.Builder dialog = createAlert(CameraActivityNew.this,
                "Camera info", "Please Allow needed permission");
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        dialog.show();
    }

    private AlertDialog.Builder createAlert(Context context, String title, String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(
                new ContextThemeWrapper(context,
                        android.R.style.Theme_Holo_Light_Dialog));
        dialog.setIcon(R.mipmap.fav_icon);
        if (title != null)
            dialog.setTitle(title);
        else
            dialog.setTitle("Information");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        return dialog;

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed() {
        releaseCamera();
        finish();
//        if (eng_img_array == null) {
//            Toast.makeText(getApplicationContext(), "Please take image", Toast.LENGTH_SHORT).show();
//        } else {
//
//        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
