package ru.rsppv.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

import ru.rsppv.criminalintent.fragment.CrimeFragment;
import ru.rsppv.criminalintent.model.Crime;
import ru.rsppv.criminalintent.model.CrimeLab;


public class CrimePagerActivity extends AppCompatActivity {
    private static final String EXTRA_CRIME_ID = "ru.rsppv.criminalintent.crime_id";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    private Button mFirstButton;
    private Button mLastButton;

    public static Intent createIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        mViewPager = (ViewPager) findViewById(R.id.crime_view_pager);
        mCrimes = CrimeLab.get(this).getCrimes();

        mFirstButton = (Button) findViewById(R.id.first_crime_button);
        mLastButton = (Button) findViewById(R.id.last_crime_button);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                super.setPrimaryItem(container, position, object);
                setButtonsEnabled(position);
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        setCurrentCrimeItem();
    }

    private void setButtonsEnabled(int position) {
        mFirstButton.setEnabled(true);
        mLastButton.setEnabled(true);
        if (position == 0) {
            mFirstButton.setEnabled(false);
        }
        if (mCrimes.size() == 0 || position == mCrimes.size() - 1) {
            mLastButton.setEnabled(false);
        }
    }

    private void setCurrentCrimeItem() {
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        int position = 0;
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                position = i;
                break;
            }
        }
        toPosition(position);

    }

    public void toFirstCrime(View view) {
        toPosition(0);
    }

    public void toLastCrime(View view) {
        int position = mCrimes.size() > 0 ? mCrimes.size() - 1 : 0;
        toPosition(position);

    }

    public void toPosition(int position) {
        mViewPager.setCurrentItem(position);
    }
}
