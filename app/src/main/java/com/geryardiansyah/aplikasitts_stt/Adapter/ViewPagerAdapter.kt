package com.geryardiansyah.aplikasitts_stt.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.geryardiansyah.aplikasitts_stt.Fragment.SttFragment
import com.geryardiansyah.aplikasitts_stt.Fragment.TtsFragment

private const val NUM_TABS = 2

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = NUM_TABS

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> SttFragment()
            else -> TtsFragment()
        }
}
