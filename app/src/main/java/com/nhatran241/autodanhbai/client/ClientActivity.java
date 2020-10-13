package com.nhatran241.autodanhbai.client;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nhatran241.autodanhbai.R;
import com.nhatran241.autodanhbai.module.screencapture.CaptureManager;

public class ClientActivity extends AppCompatActivity {
    private int requestAccessibility = 2222;
    private int requestCapture = 2223;
    private CaptureManager captureManager = CaptureManager.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        AccessibilityPermission();
    }
    private void AccessibilityPermission(){
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, requestAccessibility);
    }
    private void requestCapture() {
        captureManager.requestScreenshotPermission(this, requestCapture);
        captureManager.setOnGrantedPermissionListener(new CaptureManager.onGrantedPermissionListener() {
            @Override
            public void onResult(boolean isGranted) {
                if (isGranted) {
                    Intent intent = new Intent(ClientActivity.this,ClientAcessibilityService.class);
                    intent.setAction(ClientAcessibilityService.ACTION_START);
                    startService(intent);
                } else {
                    captureManager.requestScreenshotPermission(ClientActivity.this,requestCapture);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestAccessibility) {
            if (ClientAcessibilityService.isConnected) {
               requestCapture();
            }
        }
        if (requestCode == requestCapture) {
            captureManager.onActivityResult(resultCode, data);
        }
    }
}
