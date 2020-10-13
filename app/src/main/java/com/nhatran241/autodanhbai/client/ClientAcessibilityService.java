package com.nhatran241.autodanhbai.client;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.gesture.Gesture;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.nhatran241.autodanhbai.module.androidgestureaction.BaseActionService;
import com.nhatran241.autodanhbai.module.androidgestureaction.action.ClickAction;
import com.nhatran241.autodanhbai.module.androidgestureaction.action.ClickActionWithVerifyText;
import com.nhatran241.autodanhbai.module.screencapture.CaptureManager;
import com.nhatran241.autodanhbai.module.textfromimage.TextFromImage;


public class ClientAcessibilityService extends BaseActionService {
    public static final String ACTION_START = "actionstart";
    public static boolean isConnected=false;
    private CaptureManager captureManager;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        isConnected=true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction().equals(ACTION_START)){
            RectF verifyRectF = new RectF();
            verifyRectF.left = 116;
            verifyRectF.top = 70;
            verifyRectF.right = 200;
            verifyRectF.bottom =110;
            ClickActionWithVerifyText clickActionWithVerifyText = new ClickActionWithVerifyText(172,58,100,
                    100,5000,verifyRectF,"Ranking",false);
            PerformClickActionWithVerify(clickActionWithVerifyText, this, CaptureManager.getInstance(), TextFromImage.getInstance(), new IPerformActionWithVerifyTextListener() {
                @Override
                public void onVerifyCompleted() {

                }

                @Override
                public void onVerifyFailed() {

                }

                @Override
                public void onPerformActionCompleted(GestureDescription gestureDescription) {

                }

                @Override
                public void onPerformActionFailed(GestureDescription gestureDescription) {

                }
            });
//            captureManager = CaptureManager.getInstance();
//            captureManager.takeScreenshot(this, new CaptureManager.ICaptureManager() {
//                @Override
//                public void onTakeScreenshotCompleted(Bitmap bitmap) {
//                    Log.d(this.getClass().getName(), "onTakeScreenshotCompleted: "+bitmap.getWidth()+"/"+bitmap.getHeight());
//                }
//
//                @Override
//                public void onTakeScreenshotFailed(Exception exception) {
//                    Log.d(this.getClass().getName(),exception.getMessage().toString());
//                }
//            });
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.d(this.getClass().getName(),accessibilityEvent.toString());
    }

    @Override
    public void onInterrupt() {

    }


}
