package ru.rsppv.criminalintent.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

import ru.rsppv.criminalintent.R;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class DatePickerFragment extends DialogFragment {

    private static final String CRIME_DATE = "crimeDate";
    private static final String DATE_EXTRA = "ru.rsppv.criminalintent.fragment.datepickerfragment.date";

    private DatePicker mDatePicker;

    public static DatePickerFragment newInstance(Date crimeDate) {
        Bundle args = new Bundle();
        args.putSerializable(CRIME_DATE, crimeDate);
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(args);
        return datePickerFragment;
    }

    public static Calendar getDate(Intent intent) {
        return (Calendar) intent.getSerializableExtra(DATE_EXTRA);
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
                        Calendar date = getDate();
                        sendResult(Activity.RESULT_OK, date);
                    }

                    private Calendar getDate() {
                        Calendar instance = Calendar.getInstance();
                        instance.set(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
                        return instance;
                    }
                })
                .create();
    }

    private void initDatePicker(DatePicker datePicker, Date crimeDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(crimeDate);
        datePicker.init(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH), null);

    }

    private void sendResult(int result, Calendar resultDate) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(DATE_EXTRA, resultDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(), result, intent);
    }
}
