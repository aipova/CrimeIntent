package ru.rsppv.criminalintent.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_DIAL
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds
import android.provider.ContactsContract.Contacts
import android.support.v4.app.Fragment
import android.support.v4.app.ShareCompat
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.*
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
    private lateinit var mReportButton: Button
    private lateinit var mSuspectButton: Button
    private lateinit var mCallSuspectButton: Button
    private lateinit var mSolvedCheckBox: CheckBox

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.fragment_crime, menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crimeId = arguments!!.getSerializable(ARG_CRIME_ID) as UUID
        mCrime = CrimeLab.getInstance(activity).getCrime(crimeId)!!
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.remove_crime -> {
            removeCurrentCrime()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        mTitleField = view.findViewById(R.id.crime_title)
        with(mTitleField) {
            addTextChangedListener(crimeTitleTextWatcher)
            setText(mCrime.title)
        }

        mDateButton = view.findViewById(R.id.crime_date)
        with(mDateButton) {
            setOnClickListener {
                DatePickerFragment.newInstance(mCrime.date).show(childFragmentManager, DATE_DIALOG)
            }
            text = mCrime.dateString
        }

        mSolvedCheckBox = view.findViewById(R.id.crime_solved)
        with(mSolvedCheckBox) {
            setOnCheckedChangeListener { buttonView, isChecked -> mCrime.isSolved = isChecked }
            isChecked = mCrime.isSolved
        }

        mReportButton = view.findViewById(R.id.crime_report)
        mReportButton.setOnClickListener {
            ShareCompat.IntentBuilder.from(activity)
                .setType("text/plain")
                .setText(getCrimeReport())
                .setSubject(getString(R.string.crime_report_subject))
                .setChooserTitle(R.string.send_report).startChooser()
        }

        mSuspectButton = view.findViewById(R.id.crime_suspect)
        with(mSuspectButton) {
            val pickContact = pickContactIntent()
            setOnClickListener {
                if (contactPermissionIsNotGranted()) {
                    requestReadContactPermission()
                } else {
                    requestSuspectContact(pickContact)
                }
            }
            text = mCrime.suspect ?: text
            isEnabled = isActivityExist(pickContact)
        }

        mCallSuspectButton = view.findViewById(R.id.call_suspect)
        with(mCallSuspectButton) {
            isEnabled = !mCrime.suspectPhone.isNullOrBlank()
            setOnClickListener {
                startActivity(Intent(ACTION_DIAL).apply {
                    data = Uri.parse("tel:${mCrime.suspectPhone}")
                })
            }
        }

        return view
    }

    private fun requestSuspectContact(pickContact: Intent) {
        startActivityForResult(pickContact, REQUEST_CONTACT)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CONTACT_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestSuspectContact(pickContactIntent())
            }
        }
    }


    private fun pickContactIntent() = Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI)

    private fun requestReadContactPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.READ_CONTACTS),
            REQUEST_CONTACT_PERMISSION
        )
    }

    private fun contactPermissionIsNotGranted() =
        ContextCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.READ_CONTACTS
        ) != PackageManager.PERMISSION_GRANTED

    private fun isActivityExist(pickContact: Intent) =
        activity?.packageManager?.resolveActivity(
            pickContact,
            PackageManager.MATCH_DEFAULT_ONLY
        ) != null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CONTACT && data != null) {
            val contactUri = data.data
            val queryFields = arrayOf(Contacts.DISPLAY_NAME, Contacts.LOOKUP_KEY)
            var suspectLookup: String? = null
            var cursor = activity!!.contentResolver.query(contactUri, queryFields, null, null, null)
            cursor.run {
                if (this.count > 0) {
                    this.moveToFirst()
                    val suspect = this.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME))
                    suspectLookup = this.getString(cursor.getColumnIndex(Contacts.LOOKUP_KEY))
                    mCrime.suspect = suspect
                    mSuspectButton.text = suspect
                }
            }

            if (suspectLookup != null) {
                val phoneUri = CommonDataKinds.Phone.CONTENT_URI
                val phoneQueryFields = arrayOf(CommonDataKinds.Phone.NUMBER)
                cursor = activity!!.contentResolver.query(
                    phoneUri,
                    phoneQueryFields,
                    "${CommonDataKinds.Phone.LOOKUP_KEY} = ?",
                    arrayOf(suspectLookup),
                    null
                )
                cursor.use {
                    if (it.count > 0) {
                        it.moveToFirst()
                        val suspectPhone =
                            it.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                        mCrime.suspectPhone = suspectPhone
                        mCallSuspectButton.isEnabled = !suspectPhone.isNullOrBlank()
                    }
                }
            }
        }
    }

    override fun onCrimeDateChanged(newDate: Date) {
        mCrime.date = newDate
        mDateButton.text = mCrime.dateString
    }

    private fun getCrimeReport(): String {
        val solvedString = if (mCrime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateString = DateFormat.format(REPORT_DATE_FORMAT, mCrime.date).toString()
        val suspect = if (mCrime.suspect.isNullOrBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, mCrime.suspect)
        }

        return getString(R.string.crime_report, mCrime.title, dateString, solvedString, suspect)
    }

    override fun onPause() {
        super.onPause()

        CrimeLab.getInstance(activity).updateCrime(mCrime)
    }

    private fun removeCurrentCrime() {
        CrimeLab.getInstance(activity).removeCrime(mCrime)
        activity?.finish()

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
        const val REPORT_DATE_FORMAT = "EEE, MMM dd"
        const val REQUEST_CONTACT = 1
        const val REQUEST_CONTACT_PERMISSION = 1

        fun newInstance(crimeId: UUID): CrimeFragment {
            val bundle = Bundle().apply { putSerializable(ARG_CRIME_ID, crimeId) }
            return CrimeFragment().apply { arguments = bundle }
        }
    }
}
