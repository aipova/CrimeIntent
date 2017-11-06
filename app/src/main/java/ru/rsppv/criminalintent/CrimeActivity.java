package ru.rsppv.criminalintent;

import android.support.v4.app.Fragment;

import ru.rsppv.criminalintent.fragment.CrimeFragment;

public class CrimeActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }
}
