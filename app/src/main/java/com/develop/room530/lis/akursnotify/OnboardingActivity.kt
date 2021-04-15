package com.develop.room530.lis.akursnotify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.develop.room530.lis.akursnotify.data.database.saveRatesInDb
import com.develop.room530.lis.akursnotify.data.network.AlfaApi
import com.develop.room530.lis.akursnotify.data.network.NbrbApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

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
        CoroutineScope(Dispatchers.Main).launch {

            var date = getDateWithOffset(-64)
            val nbrb = NbrbApi.getUsdRateImpl(getDateMinusFormat(date)) //"2020-11-23"
            var akursRates = AlfaApi.getAkursRatesOnDateImpl(getDateDotFormat(date)) //"01.12.2020"
            while (akursRates.isEmpty()) {
                date = getDateWithOffset(-1, date)
                akursRates = AlfaApi.getAkursRatesOnDateImpl(getDateDotFormat(date))
            }

            saveRatesInDb(this@OnboardingActivity, akursRates, nbrb)
        }
    }
}