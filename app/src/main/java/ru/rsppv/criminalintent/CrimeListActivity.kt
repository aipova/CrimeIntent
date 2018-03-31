package ru.rsppv.criminalintent

import android.support.v4.app.Fragment

import ru.rsppv.criminalintent.fragment.CrimeListFragment

class CrimeListActivity : SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        return CrimeListFragment()
    }
}
