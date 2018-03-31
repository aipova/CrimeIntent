package ru.rsppv.criminalintent.fragment

import android.app.Dialog
import android.content.Context
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

    private lateinit var mDatePicker: DatePicker

    internal interface CrimeDateChangedListener {
        fun onCrimeDateChanged(newDate: Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val datePickerView = LayoutInflater.from(context).inflate(R.layout.dialog_date, null)
        mDatePicker = datePickerView.findViewById<View>(R.id.dialog_date_picker) as DatePicker
        initDatePicker(mDatePicker, getCrimeDateArg())
        return AlertDialog.Builder(activity as Context)
            .setView(datePickerView)
            .setTitle(R.string.date_picker_title)
            .setPositiveButton(android.R.string.ok) { dialog, whichBtn ->
                updateCrimeDate(getDatePickerDate())
            }
            .create()
    }

    private fun getDatePickerDate(): Date {
        return GregorianCalendar(mDatePicker.year, mDatePicker.month, mDatePicker.dayOfMonth).time
    }

    private fun getCrimeDateArg() = arguments?.getSerializable(CRIME_DATE) as Date

    private fun initDatePicker(datePicker: DatePicker, crimeDate: Date) {
        val calendar = Calendar.getInstance().apply { time = crimeDate }
        datePicker.init(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH), null)
    }

    private fun updateCrimeDate(resultDate: Date) {
        //        better to check type on attach and throw exception
        if (parentFragment is CrimeDateChangedListener) {
            (parentFragment as CrimeDateChangedListener).onCrimeDateChanged(resultDate)
        }
    }

    companion object {

        const val CRIME_DATE = "crimeDate"

        fun newInstance(crimeDate: Date): DatePickerFragment {
            val bundle = Bundle().apply { putSerializable(CRIME_DATE, crimeDate) }
            return DatePickerFragment().apply { arguments = bundle }
        }
    }
}
