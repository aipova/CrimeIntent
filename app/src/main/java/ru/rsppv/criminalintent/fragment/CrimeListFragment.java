package ru.rsppv.criminalintent.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import ru.rsppv.criminalintent.CrimePagerActivity;
import ru.rsppv.criminalintent.R;
import ru.rsppv.criminalintent.model.Crime;
import ru.rsppv.criminalintent.model.CrimeLab;

public class CrimeListFragment extends Fragment {
    private static final String DATE_PATTERN = "EEE, d MMM yyyy";
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (mAdapter == null) {
            CrimeLab crimeLab = CrimeLab.get(getActivity());
            mAdapter = new CrimeAdapter(crimeLab.getCrimes());
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mCrimeSolvedImageView;
        private Crime mCrime;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mCrimeSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
        }

        private void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(formatDate(crime));
            mCrimeSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        private String formatDate(Crime crime) {
            SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
            return format.format(crime.getDate());
        }

        @Override
        public void onClick(View v) {
            Intent intent = CrimePagerActivity.createIntent(getActivity(), mCrime.getId());
            startActivity(intent);
        }
    }


    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(Collection<Crime> crimes) {
            mCrimes = new ArrayList<>(crimes);
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            holder.bind(mCrimes.get(position));
        }

        public void notifyCrimeChanged(UUID crimeId) {
            Crime crime = CrimeLab.get(getActivity()).getCrime(crimeId);
            notifyItemChanged(mCrimes.indexOf(crime));
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }
}
