package ru.rsppv.criminalintent.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ru.rsppv.criminalintent.CrimePagerActivity
import ru.rsppv.criminalintent.R
import ru.rsppv.criminalintent.model.Crime
import ru.rsppv.criminalintent.model.CrimeLab
import java.util.*

class CrimeListFragment : Fragment() {
    private lateinit var mCrimeRecyclerView: RecyclerView
    private var mAdapter: CrimeAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        mCrimeRecyclerView = view.findViewById<View>(R.id.crime_recycler_view) as RecyclerView
        mCrimeRecyclerView.layoutManager = LinearLayoutManager(activity)

        updateUI()

        return view
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        mAdapter?.notifyDataSetChanged()
        if (mAdapter == null) {
            mAdapter = CrimeAdapter(CrimeLab.getInstance(activity as Context).allCrimes())
            mCrimeRecyclerView.adapter = mAdapter
        }
    }

    private inner class CrimeHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_crime, parent, false)),
        View.OnClickListener {
        private val mTitleTextView: TextView
        private val mDateTextView: TextView
        private val mCrimeSolvedImageView: ImageView
        private var mCrime: Crime? = null

        init {
            itemView.setOnClickListener(this)
            mTitleTextView = itemView.findViewById<View>(R.id.crime_title) as TextView
            mDateTextView = itemView.findViewById<View>(R.id.crime_date) as TextView
            mCrimeSolvedImageView = itemView.findViewById<View>(R.id.crime_solved) as ImageView
        }

        fun bind(crime: Crime) {
            mCrime = crime
            mTitleTextView.text = crime.title
            mDateTextView.text = crime.dateString
            mCrimeSolvedImageView.visibility = if (crime.isSolved) View.VISIBLE else View.GONE
        }

        override fun onClick(v: View) {
            val intent = CrimePagerActivity.createIntent(activity!!, mCrime!!.id)
            startActivity(intent)
        }
    }


    private inner class CrimeAdapter(crimes: Collection<Crime>) :
        RecyclerView.Adapter<CrimeHolder>() {
        private val mCrimes: List<Crime>

        init {
            mCrimes = ArrayList(crimes)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            return CrimeHolder(LayoutInflater.from(activity), parent)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            holder.bind(mCrimes[position])
        }

        override fun getItemCount(): Int {
            return mCrimes.size
        }
    }
}
