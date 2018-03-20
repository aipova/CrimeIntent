package ru.rsppv.criminalintent.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class Crime {
    private static final String DATE_PATTERN = "EEE, d MMM yyyy";
    private static final String TIME_PATTERN = "HH:mm";

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public String getDateString() {
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
        return format.format(mDate);
    }

    public String getTimeString() {
        SimpleDateFormat format = new SimpleDateFormat(TIME_PATTERN);
        return format.format(mDate);
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }
}
