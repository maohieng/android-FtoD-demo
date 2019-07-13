package edu.niptict.cs2.android.demo;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Global executor pools for the whole application.
 * <p>
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
public final class AppExecutors {

    // It should be singleton to not depends on the Application context.

    private static class LazyHolder {
        static final AppExecutors INSTANCE = new AppExecutors();
    }

    public static AppExecutors getInstance() {
        return LazyHolder.INSTANCE;
    }

    private final Executor mDiskIO;

    private final Executor mNetworkIO;

    private final Executor mMainThread;

    private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.mDiskIO = diskIO;
        this.mNetworkIO = networkIO;
        this.mMainThread = mainThread;
    }

    AppExecutors() {
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3),
                new MainThreadExecutor());
    }

    public void runOnMainThread(Runnable runnable) {
        mMainThread.execute(runnable);
    }

    public void runOnDiskIOThread(Runnable runnable) {
        mDiskIO.execute(runnable);
    }

    public void runOnNetworkIOThread(Runnable runnable) {
        mNetworkIO.execute(runnable);
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}