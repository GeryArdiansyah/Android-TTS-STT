package com.geryardiansyah.aplikasitts_stt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.geryardiansyah.aplikasitts_stt.Adapter.ViewPagerAdapter
import com.geryardiansyah.aplikasitts_stt.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

val fitur = listOf(
    "Speech to Text",
    "Text to Speech"
)

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = fitur[position]
        }.attach()
    }
}
