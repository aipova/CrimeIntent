package ru.rsppv.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

import ru.rsppv.criminalintent.fragment.CrimeFragment;

public class CrimeActivity extends SingleFragmentActivity {

    public static final String EXTRA_CRIME_ID = "u.rsppv.criminalintent.crime_id";

    public static Intent createIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }
}
