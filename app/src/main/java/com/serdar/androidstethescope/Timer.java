package com.serdar.androidstethescope;


import android.os.Handler;
import android.os.Looper;

import java.util.Formatter;
import java.util.Locale;

public class Timer {
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable runnable;

    long duration = 0L;
    long delay = 100L;

    public Timer(OnTimerTickListener listener) {
        runnable = new Runnable() {
            @Override
            public void run() {
                duration += delay;
                handler.postDelayed(runnable, delay);
                listener.onTimerTick(format());
            }
        };
    }

    public void start() {
        handler.postDelayed(runnable, delay);
    }

    public void pause() {
        handler.removeCallbacks(runnable);
    }

    public void stop() {
        handler.removeCallbacks(runnable);
        duration = 0L;
    }

    public String format() {
        long milis = (duration % 1000) / 100;
        long seconds = (duration / 1000) % 60;
        long minutes = (duration / (1000 * 60)) % 60;
        long hours = (duration / (1000 * 60 * 60));

        StringBuilder formattedString = new StringBuilder();
        Formatter formatter = new Formatter(formattedString, Locale.getDefault());

        if (hours > 0)
            formatter.format("%02d:%02d:%02d.%01d", hours, minutes, seconds, milis);
        else
            formatter.format("%02d:%02d.%01d", minutes, seconds, milis);

        return formattedString.toString();
    }

    public interface OnTimerTickListener {
        void onTimerTick(String duration);
    }
}
