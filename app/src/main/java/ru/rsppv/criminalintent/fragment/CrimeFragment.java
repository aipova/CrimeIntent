package ru.rsppv.criminalintent.fragment;

import android.app.Activity;
import android.content.Intent;
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

import java.util.UUID;

import ru.rsppv.criminalintent.R;
import ru.rsppv.criminalintent.model.Crime;
import ru.rsppv.criminalintent.model.CrimeLab;

import static android.widget.CompoundButton.OnCheckedChangeListener;


public class CrimeFragment extends Fragment {
    public static final String ARG_CRIME_ID = "crime_id";
    private Crime mCrime;

    private EditText mTitleField;
    private Button mDateButtom;
    private CheckBox mSolvedCheckBox;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static UUID changedCrimeId(Intent result) {
        return (UUID) result.getSerializableExtra(ARG_CRIME_ID);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) view.findViewById(R.id.crime_title);
        mTitleField.addTextChangedListener(new TextWatcher() {
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
        });
        mTitleField.setText(mCrime.getTitle());

        mDateButtom = (Button) view.findViewById(R.id.crime_date);
        mDateButtom.setText(mCrime.getDate().toString());
        mDateButtom.setEnabled(false);

        mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        setResult();

        return view;
    }

    private void setResult() {
        Intent result = new Intent();
        result.putExtra(ARG_CRIME_ID, mCrime.getId());
        getActivity().setResult(Activity.RESULT_OK, result);
    }
}
