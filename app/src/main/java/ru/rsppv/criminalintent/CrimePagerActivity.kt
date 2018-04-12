package ru.rsppv.criminalintent

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import ru.rsppv.criminalintent.fragment.CrimeFragment
import ru.rsppv.criminalintent.model.Crime
import ru.rsppv.criminalintent.model.CrimeLab
import java.util.*


class CrimePagerActivity : AppCompatActivity(), CrimeFragment.Callbacks {

    private lateinit var mViewPager: ViewPager
    private lateinit var mCrimes: List<Crime>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime_pager)

        mCrimes = CrimeLab.getInstance(this).getAllCrimes()

        mViewPager = findViewById<View>(R.id.crime_view_pager) as ViewPager
        mViewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return CrimeFragment.newInstance(mCrimes[position].id)
            }

            override fun getCount(): Int {
                return mCrimes.size
            }
        }

        setCurrentCrimeItem()
    }

    private fun setCurrentCrimeItem() {
        val crimeId = intent.getSerializableExtra(EXTRA_CRIME_ID) as UUID
        mViewPager.currentItem = mCrimes.indexOfFirst { it.id == crimeId }
    }

    companion object {
        private const val EXTRA_CRIME_ID = "ru.rsppv.criminalintent.crime_id"

        fun createIntent(context: Context, crimeId: UUID): Intent {
            return Intent(context, CrimePagerActivity::class.java).apply {
                putExtra(EXTRA_CRIME_ID, crimeId)
            }
        }
    }

    override fun onCrimeUpdated() {
        // behaviour isn't needed
    }

    override fun onCrimeRemoved() {
        this.finish()
    }
}
