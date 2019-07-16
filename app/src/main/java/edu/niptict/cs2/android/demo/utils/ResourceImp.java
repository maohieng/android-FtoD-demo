package edu.niptict.cs2.android.demo.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

final class ResourceImp<T> implements Resource<T> {

    private final Status status;
    private final String message;
    private final T data;
    private final Throwable cause;

    ResourceImp(@NonNull Status status, @Nullable T data, @Nullable String message, Throwable cause) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.cause = cause;
    }

    @NonNull
    @Override
    public Status getStatus() {
        return status;
    }

    @Nullable
    @Override
    public String getMessage() {
        return message;
    }

    @Nullable
    @Override
    public T getData() {
        return data;
    }

    @Nullable
    @Override
    public Throwable getCause() {
        return cause;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResourceImp<?> resource = (ResourceImp<?>) o;

        if (status != resource.status) {
            return false;
        }

        if (message != null ? !message.equals(resource.message) : resource.message != null) {
            return false;
        }

        return data != null ? data.equals(resource.data) : resource.data == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, message, data);
    }

    @Override
    public String toString() {
        return "Resource{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}