package com.develop.room530.lis.akursnotify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.develop.room530.lis.akursnotify.data.database.saveRatesInDb
import com.develop.room530.lis.akursnotify.data.network.AlfaApi
import com.develop.room530.lis.akursnotify.data.network.NbrbApi
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*

class OnboardingActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // Make sure you don't call setContentView!
//
//        // Call addSlide passing your Fragments.
//        // You can use AppIntroFragment to use a pre-built fragment
//        addSlide(
//            AppIntroFragment.newInstance(
//            title = "Welcome...",
//            description = "This is the first slide of the example"
//        ))
//        addSlide(AppIntroFragment.newInstance(
//            title = "...Let's get started!",
//            description = "This is the last slide, I won't annoy you more :)"
//        ))
//    }
//
//    override fun onSkipPressed(currentFragment: Fragment?) {
//        super.onSkipPressed(currentFragment)
//        // Decide what to do when the user clicks on "Skip"
//        finish()
//    }
//
//    override fun onDonePressed(currentFragment: Fragment?) {
//        super.onDonePressed(currentFragment)
//        // Decide what to do when the user clicks on "Done"
//        finish()
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        if (checkInternet(this) == true)
            getInitRates()

        findViewById<Button>(R.id.complete_btn).setOnClickListener {
            val sp =
                this.getSharedPreferences(this.getString(R.string.app_pref), Context.MODE_PRIVATE)
            sp.edit().apply {
                putBoolean(this@OnboardingActivity.getString(R.string.onboarding_complete), true)
                apply()
            }
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        }
    }

    private fun getInitRates() {
        lifecycleScope.launch {
            val nbrb = NbrbApi.getUsdRateHistoryImpl()
            var akursRates =
                AlfaApi.getAkursRatesOnDateImpl(getDateDotFormat(getDateWithOffset(0)))
            var i = 1
            while (isActive && i in 1..7) {
                Log.d("getInitRates", "akurs")
                akursRates = AlfaApi.getAkursRatesOnDateImpl(getDateDotFormat(getDateWithOffset(-i)))
                i++
            }

            saveRatesInDb(this@OnboardingActivity, akursRates, nbrb)
        }
    }
}