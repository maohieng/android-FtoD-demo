package edu.niptict.cs2.android.demo.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import edu.niptict.cs2.android.demo.AppExecutors;
import edu.niptict.cs2.android.demo.api.GithubService;
import edu.niptict.cs2.android.demo.api.RESTClient;
import edu.niptict.cs2.android.demo.db.AppDatabase;
import edu.niptict.cs2.android.demo.db.entity.ContributorEntity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * MainActivity to demonstrate all your logic code is in here.
 * It doesn't support the configuration change.
 * Without using ViewModel, your app business logic and your view are mess up together!
 */
public class MainActivity extends BaseListActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UIs already setup at BaseListActivity

        final AppExecutors appExecutors = AppExecutors.getInstance();
        final AppDatabase appDatabase = AppDatabase.getInstance(getApplicationContext());
        // Load data from database (synchronously, will not allowed in main thread!)
//        List<? extends Contributor> contributors = appDatabase.contributorDAO().listAll();
//        updateUI(contributors);

        // Load data from database asynchronously
        loadDataFromDatabase();

        // Load data from server
        GithubService service = RESTClient.retrofit().create(GithubService.class);
        Call<List<ContributorEntity>> contributorsCall = service.listContributors("square", "retrofit");

        // request asynchronously
        Log.i(TAG, "Loading...");
        contributorsCall.enqueue(new Callback<List<ContributorEntity>>() {
            @Override
            public void onResponse(Call<List<ContributorEntity>> call, Response<List<ContributorEntity>> response) {
                Log.i(TAG, "Loading completed.");

                if (response.isSuccessful()) {
                    Log.i(TAG, "Request successful.");

                    final List<ContributorEntity> contributors = response.body();

                    // Insert response into database (synchronously, will not allowed in main thread!)
//                    appDatabase.contributorDAO().insert(contributors);

                    // Insert response into database asynchronously
                    appExecutors.runOnDiskIOThread(new Runnable() {
                        @Override
                        public void run() {
                            appDatabase.contributorDAO().insert(contributors);

                            // re-load from database to update UI
                            appExecutors.runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadDataFromDatabase();
                                }
                            });
                        }
                    });
                } else {
                    Log.e(TAG, "Request error, code=" + response.code() + ", msg=" + response.message());
                    Toast.makeText(MainActivity.this, "Request error, code=" + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ContributorEntity>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(MainActivity.this, "Request error. Please check your connection and retry.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // @MainThread means this method must be called in main thread.

    @MainThread
    private void loadDataFromDatabase() {
        Log.i(TAG, "Load from database.");
        LiveData<List<ContributorEntity>> contributorLiveData = AppDatabase.getInstance(getApplicationContext()).contributorDAO().listAllAsync();
        contributorLiveData.observe(this, new Observer<List<ContributorEntity>>() {
            @Override
            public void onChanged(List<ContributorEntity> contributorEntities) {
                updateUI(contributorEntities);
            }
        });
    }

    @Override
    public void onRefresh() {
        // Do nothing cos we need to change a lot of code...
    }
}
