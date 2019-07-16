package edu.niptict.cs2.android.demo.utils;

import androidx.lifecycle.LiveData;

/**
 * Created by MAO Hieng on 9/14/17.
 * <p>
 * A LiveData class that have {@code null} value.
 * </p>
 */
public class AbsentLiveData extends LiveData {

    private AbsentLiveData() {
        postValue(null);
    }

    public static AbsentLiveData create() {
        return new AbsentLiveData();
    }

}