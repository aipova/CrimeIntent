package ru.rsppv.criminalintent.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import ru.rsppv.criminalintent.CrimePagerActivity
import ru.rsppv.criminalintent.R
import ru.rsppv.criminalintent.model.Crime
import ru.rsppv.criminalintent.model.CrimeLab

class CrimeListFragment : Fragment() {
    private lateinit var mCrimeRecyclerView: RecyclerView
    private var mAdapter: CrimeAdapter? = null
    private var mSubtitleVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.fragment_crime_list, menu)
        menu?.findItem(R.id.show_subtitle)?.let {
            if (mSubtitleVisible) {
                it.setTitle(R.string.hide_subtitle)
            } else {
                it.setTitle(R.string.show_subtitle)
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.new_crime -> {
            createNewCrime()
            true
        }
        R.id.show_subtitle -> {
            mSubtitleVisible = !mSubtitleVisible
            activity?.invalidateOptionsMenu()
            updateSubtitle()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible)
    }

    private fun updateSubtitle() {
        activity?.let {
            val crimeCount = CrimeLab.getInstance(it).allCrimes().size
            val subtitle =
                if (mSubtitleVisible) getString(R.string.subtitle_format, crimeCount) else null
            (it as AppCompatActivity).supportActionBar?.subtitle = subtitle
        }
    }

    private fun createNewCrime() {
        activity?.let {
            val newCrime = Crime()
            CrimeLab.getInstance(it).addCrime(newCrime)
            startActivity(CrimePagerActivity.createIntent(it, newCrime.id))
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        mCrimeRecyclerView = view.findViewById<View>(R.id.crime_recycler_view) as RecyclerView
        mCrimeRecyclerView.layoutManager = LinearLayoutManager(activity)

        savedInstanceState?.let {
            mSubtitleVisible = it.getBoolean(SAVED_SUBTITLE_VISIBLE)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        updateUI()
        updateSubtitle()
    }

    private fun updateUI() {
        activity?.let {
            mAdapter?.notifyDataSetChanged()
            if (mAdapter == null) {
                mAdapter = CrimeAdapter(CrimeLab.getInstance(it).allCrimes())
                mCrimeRecyclerView.adapter = mAdapter
            }
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
            activity?.let {
                val intent = CrimePagerActivity.createIntent(it, mCrime!!.id)
                startActivity(intent)
            }
        }
    }


    private inner class CrimeAdapter(val crimes: List<Crime>) :
        RecyclerView.Adapter<CrimeHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            return CrimeHolder(LayoutInflater.from(activity), parent)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            holder.bind(crimes[position])
        }

        override fun getItemCount(): Int {
            return crimes.size
        }
    }

    companion object {
        const val SAVED_SUBTITLE_VISIBLE = "subtitle_visibility"
    }
}
