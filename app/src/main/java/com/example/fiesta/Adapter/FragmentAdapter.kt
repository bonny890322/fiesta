package com.network.fiesta

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class FragmentAdapter(fragmentManager:FragmentManager): FragmentStatePagerAdapter(fragmentManager) {

    var mFragmentList: MutableList<Fragment> = mutableListOf()

    override fun getItem(position: Int): Fragment {
        return mFragmentList.get(position);
    }

    override fun getCount(): Int {
        return mFragmentList.size;
    }

    fun addFragment(fragment: MutableList<Fragment>) {
        mFragmentList = fragment
    }
}
