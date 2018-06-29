package com.technocom.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.technocom.imagetopdf.R;

import java.io.IOException;
import java.util.List;

/**
 * Created by janesh on 6/12/2018 : 5:52 PM.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context context;
    private final String TAG = "CameraPreview";
    private Camera.Parameters params;
    PackageManager pm;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        this.context = context;
        pm = context.getPackageManager();
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        pm = context.getPackageManager();
    }


    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            setCameraParams(Camera.Parameters.FLASH_MODE_AUTO);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    private void setCameraParams(String flash) {
        Camera.CameraInfo camInfo =
                new Camera.CameraInfo();
        Camera.getCameraInfo(getBackFacingCameraId(), camInfo);
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (camInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (camInfo.orientation - degrees + 360) % 360;
        }

        mCamera.setDisplayOrientation(result);
        params = mCamera.getParameters();
        List<Camera.Size> pictureSize = params.getSupportedPictureSizes();
        Camera.Size size = pictureSize.get((pictureSize.size() / 2) + 1);
        params.setPictureSize(size.width, size.height);
        List<Camera.Size> previewSize = params.getSupportedPreviewSizes();
        size = previewSize.get(0);
        params.setPreviewSize(size.width, size.height);

        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS))
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
            params.setFlashMode(flash);
        params.setRotation(result);
        mCamera.setParameters(params);
    }

    void setFlash(int flash) {
        switch (flash) {
            case R.id.flashOff:
                setCameraParams(Camera.Parameters.FLASH_MODE_OFF);
                break;
            case R.id.flashOn:
                setCameraParams(Camera.Parameters.FLASH_MODE_ON);
                break;
            case R.id.flashAuto:
                setCameraParams(Camera.Parameters.FLASH_MODE_AUTO);
                break;
        }
        mCamera.setParameters(params);
        mCamera.startPreview();
    }


    private int getBackFacingCameraId() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null) {
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}