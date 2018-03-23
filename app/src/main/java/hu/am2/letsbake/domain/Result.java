package hu.am2.letsbake.domain;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Result<T> {

    @NonNull
    public final ResultStatus status;
    @Nullable
    public final T data;
    @Nullable
    public final String errorMessage;

    private Result(@NonNull ResultStatus status, @Nullable T data, @Nullable String errorMessage) {
        this.status = status;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultStatus.SUCCESS, data, null);
    }

    public static <T> Result<T> loading() {
        return new Result<>(ResultStatus.LOADING, null, null);
    }

    public static <T> Result<T> error(String errorMessage) {
        return new Result<>(ResultStatus.ERROR, null, errorMessage);
    }
}
