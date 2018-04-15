package ru.rsppv.criminalintent.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import ru.rsppv.criminalintent.R
import ru.rsppv.criminalintent.model.Crime
import ru.rsppv.criminalintent.model.CrimeLab

class CrimeListFragment : Fragment() {
    private lateinit var mCrimeRecyclerView: RecyclerView
    private lateinit var mNoCrimesView: TextView
    private var mAdapter: CrimeAdapter? = null
    private var mSubtitleVisible = false
    private var mCallbacks: Callbacks? = null

    interface Callbacks {
        fun onCrimeSelected(crime: Crime)
        fun onCrimeSwiped(crime: Crime)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // should be well documented and possibly throw appropriate exception
        mCallbacks = context as Callbacks
    }

    override fun onDetach() {
        super.onDetach()
        mCallbacks = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.fragment_crime_list, menu)
        val subtitleItem = menu?.findItem(R.id.show_subtitle)
        subtitleItem?.setTitle(if (mSubtitleVisible) R.string.hide_subtitle else R.string.show_subtitle)

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
        val crimeCount = CrimeLab.getInstance(activity).getAllCrimes().size
        val subtitle =
            if (mSubtitleVisible) getSubtitle(crimeCount) else null
        (activity as AppCompatActivity).supportActionBar?.subtitle = subtitle

    }

    private fun getSubtitle(crimeCount: Int) =
        resources.getQuantityString(R.plurals.subtitle_plurals, crimeCount, crimeCount)

    private fun createNewCrime() {
        activity?.let {
            val newCrime = Crime()
            CrimeLab.getInstance(it).addCrime(newCrime)
            updateUI()
            mCallbacks?.onCrimeSelected(newCrime)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view)
        mCrimeRecyclerView.layoutManager = LinearLayoutManager(activity)
        ItemTouchHelper(mRevoceOnSwipeHandler).attachToRecyclerView(mCrimeRecyclerView)

        mNoCrimesView = view.findViewById(R.id.no_crimes_text)

        savedInstanceState?.let {
            mSubtitleVisible = it.getBoolean(SAVED_SUBTITLE_VISIBLE)
        }

        return view
    }

    private val mRevoceOnSwipeHandler =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView?,
                viewHolder: RecyclerView.ViewHolder?,
                target: RecyclerView.ViewHolder?
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                viewHolder?.let {
                    (mCrimeRecyclerView.adapter as CrimeAdapter).removeAt(it.adapterPosition)
                }
            }
        }

    override fun onResume() {
        super.onResume()
        updateUI()
        updateSubtitle()
    }

    fun updateUI() {
        val crimes = CrimeLab.getInstance(activity).getAllCrimes()
        if (mAdapter == null) {
            mAdapter = CrimeAdapter(crimes)
            mCrimeRecyclerView.adapter = mAdapter
        } else {
            mAdapter?.updateCrimes(crimes)
            mAdapter?.notifyDataSetChanged()
        }
        mNoCrimesView.visibility = if (crimes.isEmpty()) View.VISIBLE else View.GONE

    }

    private inner class CrimeHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_crime, parent, false)),
        View.OnClickListener {
        private val mTitleTextView: TextView
        private val mDateTextView: TextView
        private val mCrimeSolvedImageView: ImageView
        private lateinit var mCrime: Crime

        init {
            itemView.setOnClickListener(this)
            mTitleTextView = itemView.findViewById<View>(R.id.crime_title) as TextView
            mDateTextView = itemView.findViewById<View>(R.id.crime_date) as TextView
            mCrimeSolvedImageView = itemView.findViewById<View>(R.id.crime_solved) as ImageView
        }

        fun bind(crime: Crime) {
            mCrime = crime
            mTitleTextView.text = crime.title
            mDateTextView.text = crime.getDateString(activity)
            mCrimeSolvedImageView.visibility = if (crime.isSolved) View.VISIBLE else View.GONE
        }

        override fun onClick(v: View) {
            mCallbacks?.onCrimeSelected(mCrime)
        }
    }


    private inner class CrimeAdapter(var crimes: MutableList<Crime>) :
        RecyclerView.Adapter<CrimeHolder>() {

        fun updateCrimes(crimeList: MutableList<Crime>) {
            crimes = crimeList
        }

        fun removeAt(position: Int) {
            val crime = crimes[position]
            CrimeLab.getInstance(activity).removeCrime(crime)
            crimes.removeAt(position)
            mCallbacks?.onCrimeSwiped(crime)
            notifyItemRemoved(position)
        }

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
