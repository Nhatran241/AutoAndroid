package com.nhatran241.autodanhbai.module.textfromimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.nhatran241.autodanhbai.module.screencapture.CaptureManager;


public class TextFromImage {
    private static TextFromImage instance;
    private TextRecognizer recognizer ;

    public static TextFromImage getInstance() {
        if(instance==null)
            instance = new TextFromImage();
        return instance;
    }

//        iPerformActionWithVerifyTextListener.onPerformActionCompleted(gestureDescription);
//    TextFromImage.IGetTextListener iGetTextListener = new TextFromImage.IGetTextListener() {
//        @Override
//        public void onGetTextSuccess(String text) {
//
//        }
//
//        @Override
//        public void onGetTextFailed(String error) {
//        }
//    };
//    CaptureManager.ICaptureManagerListener iCaptureManagerListener = new CaptureManager.ICaptureManagerListener() {
//        @Override
//        public void onCaptureCompleted(Bitmap bitmap) {
//            RectF newBitmapRectf = clickActionWithVerifyText.getVerifyRectf();
//            Bitmap rectfBitmap = Bitmap.createBitmap(bitmap,(int) newBitmapRectf.left,(int)newBitmapRectf.top,(int)(newBitmapRectf.right-newBitmapRectf.left),(int)(newBitmapRectf.bottom-newBitmapRectf.top));
//
//        }
//
//        @Override
//        public void onCaptureFailed() {
//
//        }
//    };
//                captureManager.takeScreenshot(context, iCaptureManagerListener);
    public void getTextFromBitmap(Context context, CaptureManager captureManager,String text,int tryCount, IGetTextListener iGetTextListener){
        if(recognizer ==null)
            recognizer = TextRecognition.getClient();
        captureManager.takeScreenshot(context, new CaptureManager.ICaptureManagerListener() {
            @Override
            public void onCaptureCompleted(Bitmap bitmap) {
                InputImage image = InputImage.fromBitmap(bitmap, 0);
                recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text visionText) {
                        Log.d(this.getClass().getName(), "onSuccess: "+visionText.getText());
                        if(visionText.getText().contains(text)){
                            iGetTextListener.onGetTextSuccess(visionText.getText());
                        }else {
                            if(tryCount == 1){
                                iGetTextListener.onGetTextFailed("not match");
                            }else {
                                getTextFromBitmap(context,captureManager,text,tryCount-1,iGetTextListener);
                            }
                        }
                    }
                }).addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                iGetTextListener.onGetTextFailed(e.toString());
                            }
                        });
            }

            @Override
            public void onCaptureFailed() {
                iGetTextListener.onGetTextFailed("capture failed");

            }
        });

    }
    public interface IGetTextListener{
        void onGetTextSuccess(String text);
        void onGetTextFailed(String error);
    }
}
