package com.example.shivamagrawal.photoshareapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

import android.hardware.Camera;

import android.os.Bundle;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.os.*;

import java.util.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import android.content.res.Configuration;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.example.shivamagrawal.photoshareapp.Objects.Server;

@SuppressWarnings("deprecation")
public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    // TODO: allow zoom, tap-to-focus, and filters

    private Camera mCamera;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private ImageView finishCamera;
    private ImageView captureImage;
    private ImageView switchCameras;

    private Context context;

    private int currrentCamID = Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        context = this;

        surfaceView = (SurfaceView) findViewById(R.id.camera_preview_surfaceview);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(CameraActivity.this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // Buttons

        finishCamera = (ImageView) findViewById(R.id.finish_camera_button);
        finishCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishCamera();
            }
        });

        captureImage = (ImageView) findViewById(R.id.capture_image_button);
        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture();
            }
        });

        switchCameras = (ImageView) findViewById(R.id.switch_cameras_button);
        switchCameras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCameras();
            }
        });
    }

    private void openCamera() {
        // Camera should be null
        try {
            mCamera = Camera.open(currrentCamID);

            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            mCamera.setDisplayOrientation(90);

            Camera.Parameters parameters = mCamera.getParameters();

            int width = surfaceView.getWidth();
            int height = surfaceView.getHeight();
            Camera.Size size = getOptimalPreviewSize(mCamera.getParameters().getSupportedPreviewSizes(), width, height);
            parameters.setPictureSize(size.width, size.height);

            if (parameters.getFocusMode().equals("auto")) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                mCamera.setParameters(parameters);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void finishCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        finish();
    }

    private void switchCameras() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (currrentCamID == Camera.CameraInfo.CAMERA_FACING_BACK)
            currrentCamID = Camera.CameraInfo.CAMERA_FACING_FRONT;
        else currrentCamID = Camera.CameraInfo.CAMERA_FACING_BACK;
        openCamera();
    }


    private void capture() {
        mCamera.takePicture(null, null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                if (data != null)
                    new UploadPhoto().execute(data);
                mCamera.startPreview();
            }
        });
    }

    private class UploadPhoto extends AsyncTask<byte[], Void, Boolean> {
        @Override
        protected Boolean doInBackground(byte[]... params) {
            byte[] data = params[0];
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                Bitmap scaled = Bitmap.createScaledBitmap(bm, screenHeight, screenWidth, true);
                int w = scaled.getWidth();
                int h = scaled.getHeight();
                Matrix mtx = new Matrix();
                mtx.postRotate(90);
                bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
            } else { // LANDSCAPE MODE
                bm = Bitmap.createScaledBitmap(bm, screenWidth, screenHeight, true);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos); // quality doesn't matter b/c PNG is lossless
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("image", encodedImage);
            StringRequest sr = Server.POST(parameters, Server.uploadPhotoURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("RES", response.isEmpty()?"EMPTY RESPONSE": response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("ERR", "ERR");
                        }
                    }
            );
            Server.makeRequest(context, sr);

            //sendImage(encodedImage);
            //MediaStore.Images.Media.insertImage(getContentResolver(), bm, "LOL", "LOLOL"); // store in image gallery

            return true;
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        openCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Make Full Screen
        findViewById(R.id.camera_container_layout).setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}