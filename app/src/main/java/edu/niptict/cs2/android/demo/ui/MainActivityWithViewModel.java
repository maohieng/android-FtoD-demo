package edu.niptict.cs2.android.demo.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;

import edu.niptict.cs2.android.demo.model.Contributor;

/**
 * Activity that uses a ViewModel to separated its business login and UIs
 *
 * @autor MAO Hieng 7/15/2019
 */
public class MainActivityWithViewModel extends BaseListActivity {

    private static final String TAG = "MainActivityWithViewModel";

    ViewModel_Basic mViewModelBasic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UIs already setup at BaseListActivity

        mViewModelBasic = ViewModelProviders.of(this).get(ViewModel_Basic.class);
        LiveData<List<? extends Contributor>> contributorsLiveData = mViewModelBasic.getContributors();
        contributorsLiveData.observe(this, new Observer<List<? extends Contributor>>() {
            @Override
            public void onChanged(List<? extends Contributor> contributors) {
                // Dismiss refreshing
                setRefreshing(false);

                if (contributors == null) {
                    Toast.makeText(MainActivityWithViewModel.this, "No data. Check log for detail.", Toast.LENGTH_SHORT).show();
                } else {
                    updateUI(contributors);
                }
            }
        });

        // Start loading contributors
        mViewModelBasic.loadContributors(new ViewModel_Basic.ContributorParams("square", "retrofit"));

    }

    @Override
    public void onRefresh() {
        // Just call reload method that provided by ViewModel
        mViewModelBasic.reloadContributors();
    }
}
