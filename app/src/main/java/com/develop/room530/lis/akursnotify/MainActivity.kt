package com.develop.room530.lis.akursnotify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {


    // TODO add install code - load akurs! (weekend problems now) - onboarding screen in this time:)

    private var home: HomeFragment? = null
    private var chart: ChartFragment? = null
    private var settings: SettingsFragment? = null

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentById(R.id.fragment_container_view) !is HomeFragment){
            supportFragmentManager.commit {
                replace(R.id.fragment_container_view, home!!)
                attach(home!!)
            }
            findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId =
                R.id.HomeFragment
        }
        else
            super.onBackPressed()
    }

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
                            val fr =
                                supportFragmentManager.findFragmentById(R.id.fragment_container_view)
                            when (fr) {
                                is HomeFragment -> {
                                    home = fr
                                    detach(fr)
                                }
                                is SettingsFragment -> {
                                    detach(fr)
                                    settings = fr
                                }
                            }
                            if (chart == null)
                                chart = ChartFragment()
                            replace(R.id.fragment_container_view, chart!!)
                            attach(chart!!)
                        }
                    true
                }
                R.id.SettingsFragment -> {
                    if (supportFragmentManager.findFragmentById(R.id.fragment_container_view) !is SettingsFragment)
                        supportFragmentManager.commit {
                            val fr =
                                supportFragmentManager.findFragmentById(R.id.fragment_container_view)
                            when (fr) {
                                is HomeFragment -> {
                                    home = fr
                                    detach(fr)
                                }
                                is ChartFragment -> {
                                    chart = fr
                                    detach(fr)
                                }
                            }

                            if (settings == null)
                                settings = SettingsFragment()
                            replace(R.id.fragment_container_view, settings!!)
                            attach(settings!!)
                        }
                    true
                }
                R.id.HomeFragment -> {


                    if (supportFragmentManager.findFragmentById(R.id.fragment_container_view) !is HomeFragment)
                        supportFragmentManager.commit {
                            val fr =
                                supportFragmentManager.findFragmentById(R.id.fragment_container_view)
                            when (fr) {
                                is SettingsFragment -> {
                                    settings = fr
                                    detach(fr)
                                }
                                is ChartFragment -> {
                                    chart = fr
                                    detach(fr)
                                }
                            }

                            if (home == null)
                                home = HomeFragment()
                            replace(R.id.fragment_container_view, home!!)
                            attach(home!!)
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