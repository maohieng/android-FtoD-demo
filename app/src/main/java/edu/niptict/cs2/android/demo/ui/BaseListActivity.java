package edu.niptict.cs2.android.demo.ui;

import android.os.Bundle;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import edu.niptict.cs2.android.demo.R;
import edu.niptict.cs2.android.demo.model.Contributor;

/**
 * Base Activity used with R.layout.activity_main, and initialized its RecyclerView.
 * Provides a method updateUI() for convenient.
 *
 * @autor MAO Hieng 7/16/2019
 */
public abstract class BaseListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefresh);

        // Setup swipeRefreshLayout
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // Setup recyclerview
        ContributorListAdapter listAdapter = new ContributorListAdapter(this);
        mRecyclerView.setAdapter(listAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @MainThread
    protected void updateUI(List<? extends Contributor> contributors) {
        ContributorListAdapter adapter = (ContributorListAdapter) mRecyclerView.getAdapter();
        assert adapter != null; // throw if it is null
        adapter.notifyDataSetChanged(contributors);
    }

    @MainThread
    protected void setRefreshing(boolean refresh) {
        if (mSwipeRefreshLayout != null &&
                getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
            mSwipeRefreshLayout.setRefreshing(refresh);
        }
    }
}
