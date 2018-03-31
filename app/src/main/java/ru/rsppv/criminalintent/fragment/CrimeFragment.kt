package ru.rsppv.criminalintent.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import ru.rsppv.criminalintent.R
import ru.rsppv.criminalintent.model.Crime
import ru.rsppv.criminalintent.model.CrimeLab
import java.util.*


class CrimeFragment : Fragment(), DatePickerFragment.CrimeDateChangedListener {
    private lateinit var mCrime: Crime

    private lateinit var mTitleField: EditText
    private lateinit var mDateButton: Button
    private lateinit var mSolvedCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crimeId = arguments!!.getSerializable(ARG_CRIME_ID) as UUID
        mCrime = CrimeLab.get(activity).getCrime(crimeId)
    }

    override fun onCrimeDateChanged(newDate: Date) {
        mCrime.date = newDate
        mDateButton.text = mCrime.dateString
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        mTitleField = view.findViewById<View>(R.id.crime_title) as EditText
        with(mTitleField) {
            addTextChangedListener(crimeTitleTextWatcher)
            setText(mCrime.title)
        }

        mDateButton = view.findViewById<View>(R.id.crime_date) as Button
        with(mDateButton) {
            setOnClickListener {
                DatePickerFragment.newInstance(mCrime.date).show(childFragmentManager, DATE_DIALOG)
            }
            text = mCrime.dateString
        }

        mSolvedCheckBox = view.findViewById<View>(R.id.crime_solved) as CheckBox
        with(mSolvedCheckBox) {
            setOnCheckedChangeListener { buttonView, isChecked -> mCrime.isSolved = isChecked }
            isChecked = mCrime.isSolved
        }

        return view
    }

    private val crimeTitleTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            mCrime.title = s.toString()
        }

        override fun afterTextChanged(s: Editable) {}
    }

    companion object {
        const val ARG_CRIME_ID = "crime_id"
        const val DATE_DIALOG = "DateDialog"

        fun newInstance(crimeId: UUID): CrimeFragment {
            val bundle = Bundle().apply { putSerializable(ARG_CRIME_ID, crimeId) }
            return CrimeFragment().apply { arguments = bundle }
        }
    }
}
