package com.develop.room530.lis.akursnotify

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.TextView
import com.develop.room530.lis.akursnotify.databinding.GoalsHistoryCardBinding
import com.develop.room530.lis.akursnotify.features.home.FAB_ANIM_DURATION
import java.text.SimpleDateFormat
import java.util.*

fun getDateWithOffset(offset: Int, date: Date = Date()): Date {
    val cal = Calendar.getInstance()
    cal.time = date
    cal.add(Calendar.DATE, offset)
    return cal.time
}

fun getDateMinusFormat(date: Date): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("ru"))
    return sdf.format(date)
}

fun getDateDotFormat(date: Date): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))
    return sdf.format(date)
}

fun getDateDDMMFormat(date: Date): String {
    val sdf = SimpleDateFormat("dd.MM", Locale("ru"))
    return sdf.format(date)
}

fun getDateHHMMDDMMFormat(date: Date): String {
    val sdf = SimpleDateFormat("HH:mm dd.MM", Locale("ru"))
    return sdf.format(date)
}

fun getDateDDMMFormatFromLong(millis: Long): String {
    val sdf = SimpleDateFormat("HH:mm dd.MM", Locale("ru"))
    return sdf.format(Date(millis))
}

fun Float.format(digits: Int) = "%.${digits}f".format(this)

fun TextView.setRateComparingState(comparingResult: Float) {
    when {
        comparingResult < 0 -> {
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_triangle_up_yan,
                0
            )
        }
        comparingResult > 0 -> {
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_triangle_down_yan,
                0
            )
        }
        comparingResult == 0F -> {
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                0,
                0
            )
        }
    }
}


fun expand(v: View) {
    v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    val targetHeight = v.measuredHeight

    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    v.layoutParams.height = 1
    v.visibility = View.VISIBLE
    val a: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            v.layoutParams.height =
                if (interpolatedTime == 1f) LinearLayout.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
            v.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    // 1dp/ms
    a.duration = FAB_ANIM_DURATION//(targetHeight / v.context.resources.displayMetrics.density).toLong()
    v.startAnimation(a)
}

fun collapse(v: View) {
    val initialHeight = v.measuredHeight
    val a: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            if (interpolatedTime == 1f) {
                v.visibility = View.GONE
            } else {
                v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                v.requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }
    a.duration = FAB_ANIM_DURATION//a.duration = (initialHeight / v.context.resources.displayMetrics.density).toLong()
    v.startAnimation(a)
}

fun GoalsHistoryCardBinding.animatedCollapse(duration: Long){
    delimiter.animate()
        .setDuration(duration)
        .alpha(0F)
        .start()

    collapse(rates)

    goalsCardStatus.setImageResource(R.drawable.ic_baseline_arrow_down_24)
}

fun GoalsHistoryCardBinding.animatedExpand(duration: Long){
    delimiter.animate()
        .setDuration(duration)
        .alpha(1F)
        .start()

    expand(rates)

    goalsCardStatus.setImageResource(R.drawable.ic_baseline_arrow_up_24)
}