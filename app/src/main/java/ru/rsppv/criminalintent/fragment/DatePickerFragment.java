package ru.rsppv.criminalintent.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.rsppv.criminalintent.R;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class DatePickerFragment extends DialogFragment {

    interface CrimeDateChangedListener {
        void onCrimeDateChanged(Date newDate);
    }

    private static final String CRIME_DATE = "crimeDate";

    private DatePicker mDatePicker;

    public static DatePickerFragment newInstance(Date crimeDate) {
        Bundle args = new Bundle();
        args.putSerializable(CRIME_DATE, crimeDate);
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(args);
        return datePickerFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View datePickerView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);
        mDatePicker = (DatePicker) datePickerView.findViewById(R.id.dialog_date_picker);
        initDatePicker(mDatePicker, (Date)getArguments().getSerializable(CRIME_DATE));
        return new AlertDialog.Builder(getActivity())
                .setView(datePickerView)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateCrimeDate(getDate());
                    }

                    private Date getDate() {
                        return new GregorianCalendar(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth()).getTime();
                    }
                })
                .create();
    }

    private void initDatePicker(DatePicker datePicker, Date crimeDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(crimeDate);
        datePicker.init(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH), null);

    }

    private void updateCrimeDate(Date resultDate) {
//        better to check type on attach and throw exception
        if (getParentFragment() instanceof CrimeDateChangedListener) {
            ((CrimeDateChangedListener)getParentFragment()).onCrimeDateChanged(resultDate);
        }
    }
}
