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
import android.graphics.Point;

import java.util.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import android.content.res.Configuration;
import android.graphics.Matrix;
import android.content.pm.ActivityInfo;
import android.view.animation.RotateAnimation;
import android.view.animation.Animation;
import android.hardware.SensorManager;

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
    OrientationEventListener mOrientationListener;

    boolean portrait = true;
    int currentAngle = 0;

    private int currrentCamID = Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Log.d("CREATED", "LOL");

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

        mOrientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (45 <= orientation && orientation <= 135) {
                    // LANDSCAPE
                    if (portrait) {
                        finishCamera.startAnimation(getRotateAnim(-90));
                        captureImage.startAnimation(getRotateAnim(-90));
                        switchCameras.startAnimation(getRotateAnim(-90));
                        currentAngle = -90;
                    }
                    portrait = false;
                } else if(225 <= orientation && orientation <= 315) {
                    // LANDSCAPE
                    if (portrait) {
                        finishCamera.startAnimation(getRotateAnim(90));
                        captureImage.startAnimation(getRotateAnim(90));
                        switchCameras.startAnimation(getRotateAnim(90));
                        currentAngle = 90;
                    }
                    portrait = false;
                } else {
                    // PORTRAIT
                    if (!portrait) {
                        finishCamera.startAnimation(getRotateAnim(0));
                        captureImage.startAnimation(getRotateAnim(0));
                        switchCameras.startAnimation(getRotateAnim(0));
                        currentAngle = 0;
                    }
                    portrait = true;
                }
            }
        };
        if (mOrientationListener.canDetectOrientation() == true) {
            Log.d("ORIENT", "Can detect orientation");
            mOrientationListener.enable();
        } else {
            Log.d("ORIENT", "Cannot detect orientation");
            mOrientationListener.disable();
        }
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
        mOrientationListener.disable();
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
            //Log.d("ORIENTATION", getScreenOrientation() + "");


            byte[] data = params[0];
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0);
            // Orientation stuff
            Bitmap scaled = Bitmap.createScaledBitmap(bm, screenHeight, screenWidth, true);
            int w = scaled.getWidth();
            int h = scaled.getHeight();
            Matrix mtx = new Matrix();
            if (portrait) {
                mtx.postRotate(90);
                bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
            } else { // LANDSCAPE MODE
                mtx.postRotate(180);
                bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
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

            // Save image locally
            //MediaStore.Images.Media.insertImage(getContentResolver(), bm, "LOL", "LOLOL"); // store in image gallery

            return true;
        }
    }

    private RotateAnimation getRotateAnim(int change) {
        RotateAnimation rotate = new RotateAnimation(currentAngle, change,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(500);
        rotate.setFillAfter(true);
        return rotate;
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
        if (mCamera == null) { openCamera(); }
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

    // TESTING STUFF

    // http://stackoverflow.com/questions/18594602/how-to-implement-pinch-zoom-feature-for-camera-preview

    private float mDist;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get the pointer ID
        Log.d("TOUCHED", "TOUCH YAY!!!");
        Camera.Parameters params = mCamera.getParameters();
        int action = event.getAction();


        if (event.getPointerCount() > 1) {
            // handle multi-touch events
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                mDist = getFingerSpacing(event);
            } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                //mCamera.cancelAutoFocus();
                handleZoom(event, params);
            }
        } else {
            // handle single touch events
            /*if (action == MotionEvent.ACTION_UP) {
                handleFocus(event, params);
            }*/
        }
        return true;
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        mCamera.setParameters(params);
    }

    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    // currently set to auto-focus on single touch
                }
            });
        }
    }

    /** Determine the space between the first two fingers */
    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        double sqrt = Math.sqrt(x*x + y*y);
        return (float) sqrt;
    }
}