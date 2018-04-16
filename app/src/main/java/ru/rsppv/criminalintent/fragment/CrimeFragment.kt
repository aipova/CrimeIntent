package ru.rsppv.criminalintent.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.ShareCompat
import android.support.v4.content.FileProvider
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.*
import android.widget.*
import ru.rsppv.criminalintent.PictureUtils
import ru.rsppv.criminalintent.R
import ru.rsppv.criminalintent.model.Crime
import ru.rsppv.criminalintent.model.CrimeLab
import java.io.File
import java.util.*


class CrimeFragment : Fragment(), DatePickerFragment.CrimeDateChangedListener {
    private lateinit var mCrime: Crime
    private var mPhotoFile: File? = null

    private lateinit var mTitleField: EditText
    private lateinit var mDateButton: Button
    private lateinit var mReportButton: Button
    private lateinit var mSuspectButton: Button
    private lateinit var mSolvedCheckBox: CheckBox
    private lateinit var mPhotoButton: ImageButton
    private lateinit var mPhotoView: ImageView

    private var mCallbacks: Callbacks? = null

    interface Callbacks {
        fun onCrimeUpdated()
        fun onCrimeRemoved()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mCallbacks = context as Callbacks
    }

    override fun onDetach() {
        super.onDetach()
        mCallbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.fragment_crime, menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crimeId = arguments!!.getSerializable(ARG_CRIME_ID) as UUID
        mCrime = CrimeLab.getInstance(activity).getCrime(crimeId)!!
        mPhotoFile = CrimeLab.getInstance(activity).getPhotoFile(mCrime)
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
            text = mCrime.getDateString(activity)
        }

        mSolvedCheckBox = view.findViewById(R.id.crime_solved)
        with(mSolvedCheckBox) {
            setOnCheckedChangeListener { buttonView, isChecked ->
                mCrime.isSolved = isChecked
                updateCrime()
            }
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
            val pickContact = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            setOnClickListener {
                startActivityForResult(pickContact, REQUEST_CONTACT)
            }
            text = mCrime.suspect ?: text
            isEnabled = isActivityExist(pickContact)
        }

        mPhotoButton = view.findViewById(R.id.crime_camera)
        val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val canTakePhoto =
            mPhotoFile != null && captureImage.resolveActivity(activity?.packageManager) != null
        mPhotoButton.isEnabled = canTakePhoto
        mPhotoButton.setOnClickListener {
            takePhoto(captureImage)
        }

        mPhotoView = view.findViewById(R.id.crime_photo)
        mPhotoView.setOnClickListener {
            if (mPhotoFile != null) {
                PhotoViewFragment.newInstance(mPhotoFile!!).show(childFragmentManager, PHOTO_DIALOG)
            }
        }
        mPhotoView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                updatePhotoView(mPhotoView.width, mPhotoView.height)
                mPhotoView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        return view
    }

    private fun takePhoto(captureImage: Intent) {
        val uri = getPhotoFileUri()
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        grantPhotoPermissions(captureImage, uri)
        startActivityForResult(captureImage, REQUEST_PHOTO)
    }

    private fun grantPhotoPermissions(captureImage: Intent, uri: Uri?) {
        val cameraActivities = activity!!.packageManager.queryIntentActivities(
            captureImage,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        for (cameraActivity in cameraActivities) {
            activity!!.grantUriPermission(
                cameraActivity.activityInfo.packageName,
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }
    }

    private fun getPhotoFileUri(): Uri? {
        return FileProvider.getUriForFile(
            activity!!,
            FILE_AUTHORITY,
            mPhotoFile!!
        )
    }

    private fun isActivityExist(pickContact: Intent) =
        activity?.packageManager?.resolveActivity(
            pickContact,
            PackageManager.MATCH_DEFAULT_ONLY
        ) != null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CONTACT -> if (resultCode == Activity.RESULT_OK && data != null) {
                updateSuspectName(data)
                updateCrime()
            }
            REQUEST_PHOTO -> {
                activity?.revokeUriPermission(
                    getPhotoFileUri(),
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                updatePhotoView()
                updateCrime()
            }
        }

    }

    private fun updateSuspectName(data: Intent) {
        val contactUri = data.data
        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        val cursor = activity!!.contentResolver.query(contactUri, queryFields, null, null, null)
        cursor.use {
            if (it.count > 0) {
                it.moveToFirst()
                val suspect = it.getString(0)
                mCrime.suspect = suspect
                mSuspectButton.text = suspect
            }
        }
    }

    override fun onCrimeDateChanged(newDate: Date) {
        mCrime.date = newDate
        mDateButton.text = mCrime.getDateString(activity)
        updateCrime()
    }

    private fun updateCrime() {
        CrimeLab.getInstance(activity).updateCrime(mCrime)
        mCallbacks?.onCrimeUpdated()
    }

    fun getCrimeId() = mCrime.id

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

    private fun updatePhotoView() {
        mPhotoFile?.let {
            if (it.exists()) {
                val bitmap = PictureUtils.getScaledBitmap(it.path, activity!!)
                mPhotoView.setImageBitmap(bitmap)
                mPhotoView.contentDescription = getString(R.string.crime_photo_image_description)

            } else {
                mPhotoView.setImageDrawable(null)
                mPhotoView.contentDescription = getString(R.string.crime_photo_no_image_description)
            }
        }
    }

    private fun updatePhotoView(width: Int, height: Int) {
        mPhotoFile?.let {
            if (it.exists()) {
                val bitmap = PictureUtils.getScaledBitmap(it.path, width, height)
                mPhotoView.setImageBitmap(bitmap)

            } else {
                mPhotoView.setImageDrawable(null)
            }
        }
    }

    override fun onPause() {
        super.onPause()

        CrimeLab.getInstance(activity).updateCrime(mCrime)
    }

    private fun removeCurrentCrime() {
        CrimeLab.getInstance(activity).removeCrime(mCrime)
        mCallbacks?.onCrimeRemoved()
    }

    private val crimeTitleTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            mCrime.title = s.toString()
            updateCrime()
        }

        override fun afterTextChanged(s: Editable) {}
    }

    companion object {
        const val ARG_CRIME_ID = "crime_id"
        const val DATE_DIALOG = "DateDialog"
        const val PHOTO_DIALOG = "PhotoDialog"
        const val REPORT_DATE_FORMAT = "EEE, MMM dd"
        const val FILE_AUTHORITY = "ru.rsppv.criminalintent.fileprovider"
        const val REQUEST_CONTACT = 1
        const val REQUEST_PHOTO = 2

        fun newInstance(crimeId: UUID): CrimeFragment {
            val bundle = Bundle().apply { putSerializable(ARG_CRIME_ID, crimeId) }
            return CrimeFragment().apply { arguments = bundle }
        }
    }
}
