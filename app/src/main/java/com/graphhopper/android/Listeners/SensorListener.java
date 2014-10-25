package com.graphhopper.android.Listeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;

/**
 * Created by aliparsa on 10/21/2014.
 */
public class SensorListener implements SensorEventListener {
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        /*float azimuth = Math.round(sensorEvent.values[0]);
        float pitch = Math.round(sensorEvent.values[1]);
        float roll = Math.round(sensorEvent.values[2]);

        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.setFillAfter(false);
        animSet.setFillEnabled(true);

        final RotateAnimation animRotate = new RotateAnimation(-lastAzimuth, -azimuth,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotate.setDuration(200);
        animRotate.setFillAfter(false);
        animSet.addAnimation(animRotate);


        img4.startAnimation(animSet);
        lastAzimuth = azimuth;*/

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
