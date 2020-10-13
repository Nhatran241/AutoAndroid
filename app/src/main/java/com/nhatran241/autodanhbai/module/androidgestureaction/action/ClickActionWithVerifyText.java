package com.nhatran241.autodanhbai.module.androidgestureaction.action;

import android.graphics.RectF;

public class ClickActionWithVerifyText extends ClickAction {
    private RectF verifyRectf;
    private String verifyText;
    private boolean allMustMatch=false;

    public ClickActionWithVerifyText(int x, int y, long duration, long startTime, long delayTime, RectF verifyRectf, String verifyText, boolean allMustMatch) {
        super(x, y, duration, startTime, delayTime);
        this.verifyRectf = verifyRectf;
        this.verifyText = verifyText;
        this.allMustMatch = allMustMatch;
    }

    public RectF getVerifyRectf() {
        return verifyRectf;
    }

    public void setVerifyRectf(RectF verifyRectf) {
        this.verifyRectf = verifyRectf;
    }

    public String getVerifyText() {
        return verifyText;
    }

    public void setVerifyText(String verifyText) {
        this.verifyText = verifyText;
    }

    public boolean isAllMustMatch() {
        return allMustMatch;
    }

    public void setAllMustMatch(boolean allMustMatch) {
        this.allMustMatch = allMustMatch;
    }
}
