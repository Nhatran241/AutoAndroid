package com.nhatran241.autodanhbai.module.screencapture;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;


import java.io.File;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class CaptureManager {
    private static final String TAG = "ScreenshotManager";
    private static final String SCREENCAP_NAME = "screencap";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private static CaptureManager INSTANCE;
    private Intent mIntent;
    private onGrantedPermissionListener onGrantedPermissionListener;
    private MediaProjection mediaProjection;
    private ImageReader imageReader;
    private VirtualDisplay virtualDisplay;
    private int width,height;
    public static CaptureManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CaptureManager();
        }
        return INSTANCE;
    }

    public void setOnGrantedPermissionListener(CaptureManager.onGrantedPermissionListener onGrantedPermissionListener) {
        this.onGrantedPermissionListener = onGrantedPermissionListener;
    }

    public void requestScreenshotPermission(@NonNull Activity activity, int requestId) {
        if (mIntent == null) {
            Log.d(TAG, "requestScreenshotPermission");
            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            try {
                if (mediaProjectionManager != null) {
                    activity.startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), requestId);
                }
            } catch (Exception e) {
            }

        } else {
            Log.d(TAG, "requestScreenshotPermission: aaaaa");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(activity)) {
                    Log.d(TAG, "requestScreenshotPermission: false");
                    if (onGrantedPermissionListener != null)
                        onGrantedPermissionListener.onResult(false);
                } else {
                    if (onGrantedPermissionListener != null)
                        onGrantedPermissionListener.onResult(true);
                }
                Log.d(TAG, "requestScreenshotPermission: true");
            } else {
                if (onGrantedPermissionListener != null)
                    onGrantedPermissionListener.onResult(true);
            }
        }
    }


    public void onActivityResult(int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (resultCode == Activity.RESULT_OK && data != null) {
            mIntent = data;
            if (onGrantedPermissionListener != null) onGrantedPermissionListener.onResult(true);
        } else {
            if (onGrantedPermissionListener != null) onGrantedPermissionListener.onResult(false);
            mIntent = null;
        }
    }

    public boolean takeScreenshot(@NonNull final Context context,ICaptureManagerListener iCaptureManagerListener) {
        if (mIntent == null) {
            iCaptureManagerListener.onCaptureFailed();
            return false;
        }
        final MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        try {
            if (mediaProjectionManager != null) {
                mediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, mIntent);
            }
        } catch (IllegalStateException e) {
            Log.d(TAG, "takeScreenshot: mediaprojection already started");
        }
        if (mediaProjection == null)
            return false;
        final int density = context.getResources().getDisplayMetrics().densityDpi;
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        final Point size = new Point();
        display.getRealSize(size);
        width = size.x;
        height = size.y;
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 1);
        try {
            virtualDisplay = mediaProjection.createVirtualDisplay(SCREENCAP_NAME, width, height, density, VIRTUAL_DISPLAY_FLAGS, imageReader.getSurface(), new VirtualDisplay.Callback() {
                @Override
                public void onPaused() {
                    super.onPaused();
                    Log.d(TAG, "VirtualDisplay onPaused");
                }

                @Override
                public void onResumed() {
                    super.onResumed();
                    Log.d(TAG, "VirtualDisplay onResumed");
                }

                @Override
                public void onStopped() {
                    super.onStopped();
                    Log.d(TAG, "VirtualDisplay onStopped");
                }
            }, null);
        } catch (SecurityException e) {
            iCaptureManagerListener.onCaptureFailed();
        }

        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(final ImageReader reader) {
                Log.d("AppLog", "onImageAvailable");
                mediaProjection.stop();
                if (virtualDisplay != null)
                    virtualDisplay.release();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Image image = null;
                        Bitmap bitmap = null;
                        try {
                            image = reader.acquireLatestImage();
                            if (image != null) {
                                Image.Plane[] planes = image.getPlanes();
                                ByteBuffer buffer = planes[0].getBuffer();
                                int pixelStride = planes[0].getPixelStride();
                                int rowStride = planes[0].getRowStride();
                                int rowPadding = rowStride - pixelStride * width;
                                Log.d("nhatnhat", "doInBackground: " + pixelStride);
                                Log.d("nhatnhat", "doInBackground: " + rowStride);
                                Log.d("nhatnhat", "doInBackground: " + rowPadding);
                                bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                                bitmap.copyPixelsFromBuffer(buffer);
                                Bitmap newbitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
                                iCaptureManagerListener.onCaptureCompleted(newbitmap);
                                bitmap.recycle();
                                reader.close();
                            }else {
                                iCaptureManagerListener.onCaptureFailed();
                            }
                        } catch (Exception e) {
                            iCaptureManagerListener.onCaptureFailed();
                            if (bitmap != null)
                                bitmap.recycle();
                            if (reader != null)
                                reader.close();
                        }
                        if (image != null)
                            image.close();
                        if (reader != null) {
                            reader.close();
                        }
                    }
                }).start();
            }
        },null);
        mediaProjection.registerCallback(callback, null);
        return true;
    }

    private MediaProjection.Callback callback = new MediaProjection.Callback() {
        @Override
        public void onStop() {
            super.onStop();
            if (imageReader != null)
                imageReader.setOnImageAvailableListener(null, null);
            if (mediaProjection != null)
                mediaProjection.unregisterCallback(this);
        }
    };

    public void stopMediaProjection() {
        Log.d(TAG, "stopMediaProjection");
        if (mediaProjection != null) {
            if (callback != null)
                mediaProjection.unregisterCallback(callback);
            mediaProjection.stop();
            mIntent = null;
        }
    }
    public interface ICaptureManagerListener{
        void onCaptureCompleted(Bitmap bitmap);
        void onCaptureFailed();
    }

    public interface onGrantedPermissionListener {
        void onResult(boolean isGranted);
    }
}
