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
                    final LiveData<List<ContributorEntity>> dbSource = loadContributorsFromDB();

                    // observe from database change
                    result.addSource(dbSource, new Observer<List<ContributorEntity>>() {
                        @Override
                        public void onChanged(List<ContributorEntity> contributorEntities) {
                            // remove db source cos we need to observe other source for latest value update
                            result.removeSource(dbSource);

                            boolean shouldFetch = (contributorEntities == null || contributorEntities.isEmpty()) || input.forceFetch;
                            Log.i(TAG, "Should fetch from network? " + shouldFetch);
                            if (shouldFetch) {
                                fetchFromNetwork(input, result, dbSource);
                            } else {
                                // we re-attach dbSource as a new source, it will dispatch its latest value quickly
                                result.addSource(dbSource, new Observer<List<ContributorEntity>>() {
                                    @Override
                                    public void onChanged(List<ContributorEntity> contributorEntities) {
                                        result.setValue(contributorEntities);
                                    }
                                });
                            }
                        }
                    });

                    return result;
                }
            }
        });
    }

    private LiveData<List<ContributorEntity>> loadContributorsFromDB() {
        return mAppDatabase.contributorDAO().listAllAsync();
    }

    private void fetchFromNetwork(final ContributorParams inputParams,
                                  final MediatorLiveData<List<ContributorEntity>> result, LiveData<List<ContributorEntity>> dbSource) {
//        1. Call webservice
//        2. save response into DB
//        3. result observes loadDB
//        4. for error, result observes original dbSource

        Call<List<ContributorEntity>> contributorsCall = mWebService.listContributors(inputParams.owner, inputParams.repo);
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
                    Log.i(TAG, "Save response into DB.");
                    mAppExecutors.runOnDiskIOThread(new Runnable() {
                        @Override
                        public void run() {
                            mAppDatabase.contributorDAO().insert(contributors);

                            // reload from DB
                            mAppExecutors.runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Observe from DB
                                    result.addSource(loadContributorsFromDB(), new Observer<List<ContributorEntity>>() {
                                        @Override
                                        public void onChanged(List<ContributorEntity> contributorEntities) {
                                            result.setValue(contributorEntities);
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {
                    Log.e(TAG, "Request error, code=" + response.code() + ", msg=" + response.message());
                    // Back to observe original db source
                    result.addSource(dbSource, new Observer<List<ContributorEntity>>() {
                        @Override
                        public void onChanged(List<ContributorEntity> contributorEntities) {
                            result.setValue(contributorEntities);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<ContributorEntity>> call, Throwable t) {
                t.printStackTrace();
                // Back to observe original db source
                result.addSource(dbSource, new Observer<List<ContributorEntity>>() {
                    @Override
                    public void onChanged(List<ContributorEntity> contributorEntities) {
                        result.setValue(contributorEntities);
                    }
                });
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

    public void reloadContributors(boolean forceFetch) {
        ContributorParams existing = mContributorTrigger.getValue();

        if (existing != null) {
            Log.i(TAG, "reloadContributors: start reloading...");
            // Set new value to trigger re-load
            mContributorTrigger.setValue(new ContributorParams(existing.owner, existing.repo, forceFetch));
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
        private final boolean forceFetch;

        public ContributorParams(String owner, String repo, boolean forceFetch) {
            this.owner = owner;
            this.repo = repo;
            this.forceFetch = forceFetch;
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
                    this.repo.equals(params.repo) &&
                    this.forceFetch == params.forceFetch;
        }

        @Override
        public int hashCode() {
            return Objects.hash(owner, repo, forceFetch);
        }

        @NonNull
        @Override
        public String toString() {
            return "ContributorParams:{owner=" + owner + ", repo=" + repo + ", forceFetch=" + forceFetch + "}";
        }

        public String getOwner() {
            return owner;
        }

        public String getRepo() {
            return repo;
        }

        public boolean isForceFetch() {
            return forceFetch;
        }
    }
}
