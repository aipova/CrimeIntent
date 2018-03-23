package ru.rsppv.criminalintent.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

import ru.rsppv.criminalintent.R;
import ru.rsppv.criminalintent.model.Crime;
import ru.rsppv.criminalintent.model.CrimeLab;

import static android.widget.CompoundButton.OnCheckedChangeListener;


public class CrimeFragment extends Fragment implements DatePickerFragment.CrimeDateChangedListener {
    public static final String ARG_CRIME_ID = "crime_id";
    public static final String DATE_DIALOG = "DateDialog";
    private Crime mCrime;

    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Override
    public void onCrimeDateChanged(Date newDate) {
        mCrime.setDate(newDate);
        updateDate();
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDateString());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) view.findViewById(R.id.crime_title);
        mTitleField.addTextChangedListener(crimeTitleTextWatcher);
        mTitleField.setText(mCrime.getTitle());

        mDateButton = (Button) view.findViewById(R.id.crime_date);
        mDateButton.setOnClickListener(changeDateBtnClickListener);
        updateDate();

        mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setOnCheckedChangeListener(crimeSolvedCheckBtnListener);
        mSolvedCheckBox.setChecked(mCrime.isSolved());

        return view;
    }

    private TextWatcher crimeTitleTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mCrime.setTitle(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private View.OnClickListener changeDateBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
            dialog.show(getChildFragmentManager(), DATE_DIALOG);
        }
    };

    private OnCheckedChangeListener crimeSolvedCheckBtnListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mCrime.setSolved(isChecked);
        }
    };
}
