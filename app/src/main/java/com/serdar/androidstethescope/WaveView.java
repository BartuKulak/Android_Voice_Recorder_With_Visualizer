package com.serdar.androidstethescope;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WaveView extends View {
    Paint paint = new Paint();
    ArrayList<Float> amplitudes = new ArrayList();
    List<RectF> spikes = new ArrayList();

    float r = 6f;
    float xw = 9f;
    float sxw = 0f;
    float xsh = 400f;
    float xd = 6f;

    int maxSpikes = 0;

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.rgb(0, 128, 0));
        sxw = (float) getResources().getDisplayMetrics().widthPixels;

        maxSpikes = (int) (sxw / (xw + xd));
    }

    public void addAmplitude(Float amp) {
        Float norm = (float) Math.min(amp.intValue() / 7, 400);
        amplitudes.add(norm);
        spikes.clear();
        List<Float> amps = new ArrayList<>();
        if (maxSpikes < amplitudes.size()) {
            for (int index = maxSpikes; index > 0; index--) {
                amps.add(amplitudes.get(amplitudes.size() - 1 - index));
            }
        } else {
            for (Float tempAmp : amplitudes) {
                amps.add(tempAmp);
            }
        }

        for (int index = 0; index < amps.size(); index++) {
            float left = sxw - index * (xw + xd);
            float top = xsh / 2 - amps.get(index) / 2;
            float right = left + xw;
            float bottom = top + amps.get(index).floatValue();
            spikes.add(new RectF(left, top, right, bottom));
        }

        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        for (RectF spike : spikes) {
            canvas.drawRoundRect(spike, r, r, paint);
        }
    }

    public ArrayList<Float> clear() {
        ArrayList<Float> tempAmps = (ArrayList<Float>) amplitudes.clone();
        amplitudes.clear();
        spikes.clear();
        invalidate();
        return tempAmps;
    }
}
