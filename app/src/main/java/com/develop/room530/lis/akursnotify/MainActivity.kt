package com.develop.room530.lis.akursnotify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2

    private inner class MyPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 3

        // TODO add install code - load akurs! (weekend problems now) - onboarding screen in this time:)
        // TODO add feature get rates by date!!!!
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AkursFragment()
                1 -> HomeFragment()
                else -> NbrbFragment()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showOnboardingIfNeeded()

        viewPager = findViewById(R.id.pager)
        val pagerAdapter = MyPagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.setCurrentItem(1, false)

        val tabLayout = findViewById<TabLayout>(R.id.tab)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Альфа"
                1 -> "Сводка"
                else -> "НБ РБ"
            }
        }.attach()
    }

    private fun showOnboardingIfNeeded() {
        val sp = this.getSharedPreferences(this.getString(R.string.app_pref), Context.MODE_PRIVATE)
        if(!sp.getBoolean(this.getString(R.string.onboarding_complete), false)){
            val onboardingIntent = Intent(this, OnboardingActivity::class.java)
            startActivity(onboardingIntent)
            finish()
        }
    }
}