package edu.niptict.cs2.android.demo.ui;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.Objects;

import edu.niptict.cs2.android.demo.AppExecutors;
import edu.niptict.cs2.android.demo.api.GithubService;
import edu.niptict.cs2.android.demo.api.RESTClient;
import edu.niptict.cs2.android.demo.db.AppDatabase;
import edu.niptict.cs2.android.demo.db.entity.ContributorEntity;
import edu.niptict.cs2.android.demo.model.Contributor;
import edu.niptict.cs2.android.demo.utils.AbsentLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Basic ViewModel used to load {@link Contributor}s from database and from webservice.
 *
 * @autor MAO Hieng 7/15/2019
 */
public class ViewModel_Basic extends AndroidViewModel {

    private static final String TAG = "ViewModel_Basic";

    private final AppDatabase mAppDatabase;
    private final GithubService mWebService;
    private final AppExecutors mAppExecutors;

    private final MutableLiveData<ContributorParams> mContributorTrigger = new MutableLiveData<>();
    private final LiveData<List<ContributorEntity>> mContributorsLiveData;

    public ViewModel_Basic(@NonNull Application application) {
        super(application);
        this.mAppDatabase = AppDatabase.getInstance(application);
        mWebService = RESTClient.retrofit().create(GithubService.class);
        mAppExecutors = AppExecutors.getInstance();

        // mContributorsLiveData will always load from database when mContributorTrigger has changed its value by setValue()
        mContributorsLiveData = Transformations.switchMap(mContributorTrigger, new Function<ContributorParams, LiveData<List<ContributorEntity>>>() {
            @Override
            public LiveData<List<ContributorEntity>> apply(final ContributorParams input) {
                if (input == null) {
                    return AbsentLiveData.create();
                } else {
                    final MediatorLiveData<List<ContributorEntity>> result = new MediatorLiveData<>();

                    Log.i(TAG, "Load from database.");
                    LiveData<List<ContributorEntity>> dbLiveData = mAppDatabase.contributorDAO().listAllAsync();

                    // observe from database change
                    result.addSource(dbLiveData, new Observer<List<ContributorEntity>>() {
                        @Override
                        public void onChanged(List<ContributorEntity> contributorEntities) {
                            result.setValue(contributorEntities);
                        }
                    });

                    Call<List<ContributorEntity>> contributorsCall = mWebService.listContributors(input.owner, input.repo);
                    // request asynchronously
                    Log.i(TAG, "Request from server...");
                    contributorsCall.enqueue(new Callback<List<ContributorEntity>>() {
                        @Override
                        public void onResponse(Call<List<ContributorEntity>> call, Response<List<ContributorEntity>> response) {
                            Log.i(TAG, "Loading completed.");

                            if (response.isSuccessful()) {
                                Log.i(TAG, "Request successful.");

                                final List<ContributorEntity> contributors = response.body();

                                // Insert response into database asynchronously
                                mAppExecutors.runOnDiskIOThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAppDatabase.contributorDAO().insert(contributors);

                                        // This will trigger change to mContributorsLiveData cos it observing database change
                                    }
                                });
                            } else {
                                Log.e(TAG, "Request error, code=" + response.code() + ", msg=" + response.message());
                                result.setValue(null);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<ContributorEntity>> call, Throwable t) {
                            t.printStackTrace();
                            result.setValue(null);
                        }
                    });

                    return result;
                }
            }
        });
    }

    public LiveData<List<? extends Contributor>> getContributors() {
//        https://docs.oracle.com/javase/tutorial/java/generics/subtyping.html
        return (LiveData<List<? extends Contributor>>) (LiveData<?>) mContributorsLiveData;
    }

    public void loadContributors(@NonNull ContributorParams newParams) {
        ContributorParams existing = mContributorTrigger.getValue();

        if (existing != null && existing.equals(newParams)) {
            Log.i(TAG, "Already contributors with " + newParams);
            return;
        }

        mContributorTrigger.setValue(newParams);
    }

    public void reloadContributors() {
        ContributorParams existing = mContributorTrigger.getValue();

        if (existing != null) {
            Log.i(TAG, "reloadContributors: start reloading...");
            // Set new value to trigger re-load
            mContributorTrigger.setValue(new ContributorParams(existing.owner, existing.repo));
        } else {
            Log.e(TAG, "reloadContributors: please load contributors instead.");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Internal Classes
    ///////////////////////////////////////////////////////////////////////////

    public static class ContributorParams {
        private final String owner;
        private final String repo;

        public ContributorParams(String owner, String repo) {
            this.owner = owner;
            this.repo = repo;
        }

        // Implement to use equal()


        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == null)
                return false;

            if (this == obj)
                return true;

            if (!(obj instanceof ContributorParams))
                return false;

            ContributorParams params = (ContributorParams) obj;

            return this.owner.equals(params.owner) &&
                    this.repo.equals(params.repo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(owner, repo);
        }

        @NonNull
        @Override
        public String toString() {
            return "ContributorParams:{owner=" + owner + ", repo=" + repo + "}";
        }

        public String getOwner() {
            return owner;
        }

        public String getRepo() {
            return repo;
        }
    }
}
