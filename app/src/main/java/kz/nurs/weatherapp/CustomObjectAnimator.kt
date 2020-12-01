package kz.nurs.weatherapp

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.BounceInterpolator

class CustomObjectAnimator {
    companion object {
        fun animateTab(refresh: View) {
            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.9f, 1.2f)
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.9f, 1.2f)
            val objectAnimator = ObjectAnimator.ofPropertyValuesHolder(refresh, scaleX, scaleY)
            objectAnimator.interpolator = BounceInterpolator()
            objectAnimator.duration=800
            objectAnimator.repeatCount=ValueAnimator.INFINITE
            objectAnimator.start()
        }
    }
}