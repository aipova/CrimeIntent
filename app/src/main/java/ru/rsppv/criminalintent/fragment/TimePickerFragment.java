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
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import ru.rsppv.criminalintent.R;

public class TimePickerFragment extends DialogFragment {

    private static final String CRIME_DATE = "crimeDate";
    private static final String TIME_EXTRA = "ru.rsppv.criminalintent.fragment.timepickerfragment.time";

    private TimePicker mTimePicker;

    public static Calendar getTime(Intent intent) {
        return (Calendar) intent.getSerializableExtra(TIME_EXTRA);
    }

    public static TimePickerFragment getInstance(Date crimeDate) {
        Bundle args = new Bundle();
        args.putSerializable(CRIME_DATE, crimeDate);
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setArguments(args);
        return timePickerFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View timeDialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        mTimePicker = (TimePicker) timeDialogView.findViewById(R.id.dialog_time_picker);
        initTime();
        return new AlertDialog.Builder(getActivity())
                .setView(timeDialogView)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getTargetFragment() != null) {
                            Calendar instance = Calendar.getInstance();
                            instance.set(Calendar.HOUR, mTimePicker.getCurrentHour());
                            instance.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
                            Intent intent = new Intent();
                            intent.putExtra(TIME_EXTRA, instance);
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        }
                    }
                }).create();
    }

    private void initTime() {
        Date crimeDate = (Date) this.getArguments().getSerializable(CRIME_DATE);
        Calendar instance = Calendar.getInstance();
        instance.setTime(crimeDate);
        mTimePicker.setCurrentHour(instance.get(Calendar.HOUR));
        mTimePicker.setCurrentMinute(instance.get(Calendar.MINUTE));
    }
}
