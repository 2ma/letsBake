package hu.am2.letsbake.domain;


import java.util.Collections;
import java.util.List;

public class Result<T> {

    public final ResultStatus status;
    public final List<T> data;
    public final String errorMessage;

    private Result(ResultStatus status, List<T> data, String errorMessage) {
        this.status = status;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static <T> Result<T> success(List<T> data) {
        return new Result<>(ResultStatus.SUCCESS, data, null);
    }

    public static <T> Result<T> loading() {
        return new Result<>(ResultStatus.LOADING, Collections.emptyList(), null);
    }

    public static <T> Result<T> error(String errorMessage) {
        return new Result<>(ResultStatus.ERROR, Collections.emptyList(), errorMessage);
    }
}
