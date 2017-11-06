package ru.rsppv.criminalintent;

import android.support.v4.app.Fragment;

import ru.rsppv.criminalintent.fragment.CrimeListFragment;

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
