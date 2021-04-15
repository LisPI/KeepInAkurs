package com.develop.room530.lis.akursnotify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {


    // TODO add install code - load akurs! (weekend problems now) - onboarding screen in this time:)
    // TODO add feature get rates by date!!!!
    // TODO view setting - 3 days, weak, month - without title

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showOnboardingIfNeeded()

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<HomeFragment>(R.id.fragment_container_view)
            }
            findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId =
                R.id.HomeFragment
        }

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.ChartsFragment -> {
                    if (supportFragmentManager.findFragmentById(R.id.fragment_container_view) !is ChartFragment)
                        supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            replace<ChartFragment>(R.id.fragment_container_view)
                            addToBackStack(null)
                        }
                    true
                }
                R.id.SettingsFragment -> {
                    if (supportFragmentManager.findFragmentById(R.id.fragment_container_view) !is SettingsFragment)
                        supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            replace<SettingsFragment>(R.id.fragment_container_view)
                            addToBackStack(null)
                        }
                    true
                }
                R.id.HomeFragment -> {
                    if (supportFragmentManager.findFragmentById(R.id.fragment_container_view) !is HomeFragment)
                        supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            replace<HomeFragment>(R.id.fragment_container_view)
                            addToBackStack(null)
                        }
                    true
                }
                else -> false
            }
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