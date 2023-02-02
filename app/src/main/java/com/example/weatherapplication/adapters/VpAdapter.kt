package com.example.weatherapplication.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class VpAdapter(fragmentActivity: FragmentActivity,
                private val listOfFragments: List<Fragment>) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return listOfFragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return listOfFragments[position]
    }
}