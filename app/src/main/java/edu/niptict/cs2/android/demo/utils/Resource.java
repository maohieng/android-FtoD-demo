package edu.niptict.cs2.android.demo.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A generic class that holds a value with its loading status.
 *
 * @param <T>
 * @author MAO Hieng on 1/8/19.
 */
public interface Resource<T> {

    /**
     * Status of a resource that is provided to the UI.
     * <p>
     * These are usually created by the Repository classes where they return
     * {@code LiveData<Resource<T>>} to pass back the latest data to the UI with its fetch status.
     */
    public static enum Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    public static final class Factory {

        public static <T> Resource<T> success(@Nullable T data) {
            return new ResourceImp<T>(Status.SUCCESS, data, null, null);
        }

        public static <T> Resource<T> loading() {
            return new ResourceImp<>(Status.LOADING, null, null, null);
        }

        public static <T> Resource<T> error(String message, Throwable cause) {
            return new ResourceImp<>(Status.ERROR, null, message, cause);
        }

    }

    @NonNull
    public Status getStatus();

    @Nullable
    public String getMessage();

    @Nullable
    public Throwable getCause();

    @Nullable
    public T getData();

}
