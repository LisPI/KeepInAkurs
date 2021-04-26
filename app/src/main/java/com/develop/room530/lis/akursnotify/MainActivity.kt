package com.develop.room530.lis.akursnotify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commitNow
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val HOME = "home"
private const val SETTINGS = "settings"
private const val CHART = "chart"

class MainActivity : AppCompatActivity() {


    // TODO add install code - load akurs! (weekend problems now) - onboarding screen in this time:)
    override fun onBackPressed() { // FIXME change theme bug
        if(supportFragmentManager.findFragmentById(R.id.fragment_container_view)?.tag != HOME) {
            changeScreen(HOME)
            findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId =
                R.id.HomeFragment
        } else
            super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showOnboardingIfNeeded()

        if (savedInstanceState == null) {
            supportFragmentManager.commitNow {
                add<HomeFragment>(R.id.fragment_container_view, HOME)
            }
            findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId =
                R.id.HomeFragment
        }

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.ChartsFragment -> if(supportFragmentManager.findFragmentById(R.id.fragment_container_view)?.tag != CHART) changeScreen(CHART)
                R.id.HomeFragment -> if(supportFragmentManager.findFragmentById(R.id.fragment_container_view)?.tag != HOME) changeScreen(HOME)
                R.id.SettingsFragment -> if(supportFragmentManager.findFragmentById(R.id.fragment_container_view)?.tag != SETTINGS) changeScreen(SETTINGS)
            }
            true
        }
    }

    private fun changeScreen(newScreenTag: String) {
        supportFragmentManager.findFragmentById(R.id.fragment_container_view)?.let {
            supportFragmentManager.beginTransaction()
                .detach(it)
                .commitNow()
        }
        val requestedFragment = supportFragmentManager.findFragmentByTag(newScreenTag)
        if (requestedFragment == null) {
            when (newScreenTag) {
                HOME -> supportFragmentManager.beginTransaction()
                    .add<HomeFragment>(R.id.fragment_container_view, HOME).commitNow()
                SETTINGS -> supportFragmentManager.beginTransaction()
                    .add<SettingsFragment>(R.id.fragment_container_view, SETTINGS).commitNow()
                CHART -> supportFragmentManager.beginTransaction()
                    .add<ChartFragment>(R.id.fragment_container_view, CHART).commitNow()
            }
        } else {
            supportFragmentManager.beginTransaction().attach(requestedFragment).commitNow()
        }
    }

    private fun showOnboardingIfNeeded() {
        val sp = this.getSharedPreferences(this.getString(R.string.app_pref), Context.MODE_PRIVATE)
        if (!sp.getBoolean(this.getString(R.string.onboarding_complete), false)) {
            val onboardingIntent = Intent(this, OnboardingActivity::class.java)
            startActivity(onboardingIntent)
            finish()
        }
    }
}


// FIXME debug code
//            chart = ChartFragment()
//            settings = SettingsFragment()
//
//            Log.d("1", supportFragmentManager.findFragmentById(R.id.fragment_container_view).toString())
//            supportFragmentManager.beginTransaction().add(R.id.fragment_container_view, chart!!,"chart").commitNow()
//            Log.d("2", supportFragmentManager.findFragmentById(R.id.fragment_container_view).toString())
//            supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("chart")!!).commitNow()
//            Log.d("3", supportFragmentManager.findFragmentById(R.id.fragment_container_view).toString())
//            supportFragmentManager.beginTransaction().add(R.id.fragment_container_view, settings!!).commitNow()
//            Log.d("4", supportFragmentManager.findFragmentById(R.id.fragment_container_view).toString())
//            supportFragmentManager.beginTransaction().detach(settings!!).commitNow()
//            Log.d("5", supportFragmentManager.findFragmentById(R.id.fragment_container_view).toString())