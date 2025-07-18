package com.github.hank9999.useblessingskin.shared.model;

import com.github.hank9999.useblessingskin.shared.ErrorCode;

public class Result<T> {
    private final boolean success;
    private final T data;
    private final ErrorCode errorCode;

    private Result(boolean success, T data, ErrorCode errorCode) {
        this.success = success;
        this.data = data;
        this.errorCode = errorCode;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, data, null);
    }

    public static <T> Result<T> failure(ErrorCode errorCode) {
        return new Result<>(false, null, errorCode);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}