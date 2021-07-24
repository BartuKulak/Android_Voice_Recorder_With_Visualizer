package com.serdar.androidstethescope;

public class AudioModel {
    String path;
    String name;
    String duration;
    int durationMs;

    public AudioModel(String path, String name, String duration, int durationMs) {
        this.path = path;
        this.name = name;
        this.duration = duration;
        this.durationMs = durationMs;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(int durationMs) {
        this.durationMs = durationMs;
    }
}