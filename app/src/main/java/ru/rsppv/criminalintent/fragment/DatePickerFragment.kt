package ru.rsppv.criminalintent.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import ru.rsppv.criminalintent.R
import java.util.*
import java.util.Calendar.*

class DatePickerFragment : DialogFragment() {

    private var mDatePicker: DatePicker? = null

    internal interface CrimeDateChangedListener {
        fun onCrimeDateChanged(newDate: Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val datePickerView = LayoutInflater.from(context).inflate(R.layout.dialog_date, null)
        mDatePicker = datePickerView.findViewById<View>(R.id.dialog_date_picker) as DatePicker
        initDatePicker(mDatePicker, arguments.getSerializable(CRIME_DATE) as Date)
        return AlertDialog.Builder(activity)
                .setView(datePickerView)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, object : DialogInterface.OnClickListener {

                    private val date: Date
                        get() = GregorianCalendar(mDatePicker!!.year, mDatePicker!!.month, mDatePicker!!.dayOfMonth).time

                    override fun onClick(dialog: DialogInterface, which: Int) {
                        updateCrimeDate(date)
                    }
                })
                .create()
    }

    private fun initDatePicker(datePicker: DatePicker?, crimeDate: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = crimeDate
        datePicker!!.init(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH), null)

    }

    private fun updateCrimeDate(resultDate: Date) {
        //        better to check type on attach and throw exception
        if (parentFragment is CrimeDateChangedListener) {
            (parentFragment as CrimeDateChangedListener).onCrimeDateChanged(resultDate)
        }
    }

    companion object {

        private val CRIME_DATE = "crimeDate"

        fun newInstance(crimeDate: Date): DatePickerFragment {
            val args = Bundle()
            args.putSerializable(CRIME_DATE, crimeDate)
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.arguments = args
            return datePickerFragment
        }
    }
}
