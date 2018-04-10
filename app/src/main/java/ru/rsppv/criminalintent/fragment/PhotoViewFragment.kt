package ru.rsppv.criminalintent.fragment

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.ImageView
import ru.rsppv.criminalintent.PictureUtils
import ru.rsppv.criminalintent.R
import java.io.File

class PhotoViewFragment : DialogFragment() {
    private lateinit var mPhotoView: ImageView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_image, null)
        mPhotoView = view.findViewById(R.id.crime_photo_dialog)
        updatePhotoView(getFileExtra())
        return AlertDialog.Builder(activity!!).setView(view).create()
    }

    fun getFileExtra() = arguments?.getSerializable(PHOTO_FILE_EXTRA) as File

    private fun updatePhotoView(photoFile: File) {
        photoFile?.let {
            if (it.exists()) {
                val bitmap = PictureUtils.getScaledBitmap(it.path, activity!!)
                mPhotoView.setImageBitmap(bitmap)

            } else {
                mPhotoView.setImageDrawable(null)
            }
        }
    }

    companion object {
        const val PHOTO_FILE_EXTRA = "photoFile"

        fun newInstance(photoFile: File): PhotoViewFragment {
            val bundle = Bundle().apply { putSerializable(PHOTO_FILE_EXTRA, photoFile) }
            return PhotoViewFragment().apply { arguments = bundle }
        }
    }
}