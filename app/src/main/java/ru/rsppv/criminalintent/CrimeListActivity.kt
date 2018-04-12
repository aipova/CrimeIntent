package ru.rsppv.criminalintent

import android.support.v4.app.Fragment
import android.view.View
import ru.rsppv.criminalintent.fragment.CrimeFragment
import ru.rsppv.criminalintent.fragment.CrimeListFragment
import ru.rsppv.criminalintent.model.Crime

class CrimeListActivity :
    SingleFragmentActivity(), CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    override fun getLayoutResId(): Int {
        return R.layout.activity_masterdetail
    }

    override fun createFragment(): Fragment {
        return CrimeListFragment()
    }

    override fun onCrimeSelected(crime: Crime) {
        if (findViewById<View>(R.id.detail_fragment_container) == null) {
            val intent = CrimePagerActivity.createIntent(this, crime.id)
            startActivity(intent)
        } else {
            val newDetail = CrimeFragment.newInstance(crime.id)

            supportFragmentManager.beginTransaction()
                .replace(R.id.detail_fragment_container, newDetail)
                .commit()
        }
    }

    override fun onCrimeUpdated() {
        updateCrimeListUI()
    }

    private fun updateCrimeListUI() {
        val crimeListFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as CrimeListFragment
        crimeListFragment.updateUI()
    }

    override fun onCrimeRemoved() {
        val detailsFragment =
            supportFragmentManager.findFragmentById(R.id.detail_fragment_container)
        if (detailsFragment != null) {
            supportFragmentManager.beginTransaction()
                .remove(detailsFragment)
                .commit()
            updateCrimeListUI()
        }
    }

    override fun onCrimeSwiped(crime: Crime) {
        val detailsFragment =
            supportFragmentManager.findFragmentById(R.id.detail_fragment_container)
        if (detailsFragment is CrimeFragment) {
            if (detailsFragment.getCrimeId() == crime.id) {
                supportFragmentManager.beginTransaction()
                    .remove(detailsFragment)
                    .commit()
            }
        }

    }
}
