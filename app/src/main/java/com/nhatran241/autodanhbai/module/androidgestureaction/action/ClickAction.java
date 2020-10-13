package com.nhatran241.autodanhbai.module.androidgestureaction.action;


import android.graphics.Path;

public class ClickAction {
    private Path path = new Path();
    private long duration;
    private long startTime;
    private long delayTime;

    public ClickAction(int x,int y, long duration, long startTime, long delayTime) {
        path.moveTo(x,y);
        this.duration = duration;
        this.startTime = startTime;
        this.delayTime = delayTime;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }
}
