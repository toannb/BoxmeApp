package com.shipchung.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 8/3/2015.
 */
public class LoadingView extends ImageView {
    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setAnimation(attrs);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAnimation(attrs);
    }

    public LoadingView(Context context) {
        super(context);
    }

    private void setAnimation(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ProgressView);
        int frameCount = a.getInt(R.styleable.ProgressView_frameCount, 12);
        int duration = a.getInt(R.styleable.ProgressView_duration, 1500);
        a.recycle();

        setAnimation(frameCount, duration);
    }

    public void setAnimation(final int frameCount, final int duration) {
        Animation a = AnimationUtils.loadAnimation(getContext(), R.anim.loading_anim);
        a.setDuration(duration);
        a.setInterpolator(new Interpolator() {

            @Override
            public float getInterpolation(float input) {
                return (float) Math.floor(input * frameCount) / frameCount;
            }
        });
        startAnimation(a);
    }
}
