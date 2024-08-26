package com.example.newzz.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.newzz.screens.explore.EntertainmentFragment
import com.example.newzz.screens.explore.PoliticsFragment
import com.example.newzz.screens.explore.SportsFragment
import com.example.newzz.screens.explore.TechnologyFragment
import com.example.newzz.screens.explore.TrendingFragment

class ExplorePagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TrendingFragment()
            1 -> TechnologyFragment()
            2 -> SportsFragment()
            3 -> EntertainmentFragment()
            4 -> PoliticsFragment()
            else -> Fragment()
        }
    }
}