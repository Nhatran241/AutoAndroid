package com.nhatran241.autodanhbai.module.androidgestureaction;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;
import android.widget.Toast;

import com.nhatran241.autodanhbai.client.ClientAcessibilityService;
import com.nhatran241.autodanhbai.module.androidgestureaction.action.ClickAction;
import com.nhatran241.autodanhbai.module.androidgestureaction.action.ClickActionWithVerifyText;
import com.nhatran241.autodanhbai.module.screencapture.CaptureManager;
import com.nhatran241.autodanhbai.module.textfromimage.TextFromImage;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;

public abstract class BaseActionService extends AccessibilityService {
    public void PerformClickAction(final ClickAction clickAction, final IPerformActionListener iPerformActionListener){
         Observable.timer(clickAction.getDelayTime(), TimeUnit.MILLISECONDS).doOnComplete(new Action() {
            @Override
            public void run() {
                GestureDescription.Builder builder = new GestureDescription.Builder();
                builder.addStroke(new GestureDescription.StrokeDescription(clickAction.getPath(),clickAction.getStartTime(),clickAction.getDuration()));
                dispatchGesture(builder.build(), new GestureResultCallback() {
                    @Override
                    public void onCompleted(GestureDescription gestureDescription) {
                        super.onCompleted(gestureDescription);
                        iPerformActionListener.onPerformActionCompleted(gestureDescription);
                    }

                    @Override
                    public void onCancelled(GestureDescription gestureDescription) {
                        super.onCancelled(gestureDescription);
                        iPerformActionListener.onPerformActionFailed(gestureDescription);
                    }
                },null);
            }
        }).observeOn(Schedulers.io()).subscribe();

    }
    public void PerformClickActionWithVerify(ClickActionWithVerifyText clickActionWithVerifyText, Context context, CaptureManager captureManager, TextFromImage textFromImage, IPerformActionWithVerifyTextListener iPerformActionWithVerifyTextListener){
        PerformClickAction(clickActionWithVerifyText, new IPerformActionListener() {
            @Override
            public void onPerformActionCompleted(GestureDescription gestureDescription) {
                textFromImage.getTextFromBitmap(context,captureManager,clickActionWithVerifyText.getVerifyText(),10 , new TextFromImage.IGetTextListener() {
                    @Override
                    public void onGetTextSuccess(String text) {
                        Log.d(this.getClass().getName(), "onGetTextSuccess: "+text);
                    }

                    @Override
                    public void onGetTextFailed(String error) {
                        Log.d(this.getClass().getName(), "onGetTextFailed: "+error);
                    }
                });

            }

            @Override
            public void onPerformActionFailed(GestureDescription gestureDescription) {
                iPerformActionWithVerifyTextListener.onPerformActionFailed(gestureDescription);
            }
        });
    }
    public interface IPerformActionListener{
        void onPerformActionCompleted(GestureDescription gestureDescription);
        void onPerformActionFailed(GestureDescription gestureDescription);
    }
    public interface IPerformActionWithVerifyTextListener extends IPerformActionListener{
        void onVerifyCompleted();
        void onVerifyFailed();
    }
}
