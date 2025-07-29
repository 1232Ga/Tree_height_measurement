package com.garudauav.forestrysurvey.network;

public abstract class SealedNetworkResult<T> {
    private SealedNetworkResult() {}

    public static final class Success<T> extends SealedNetworkResult<T> {
        private final T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    public static final class Error<T> extends SealedNetworkResult<T> {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static final class Loading<T> extends SealedNetworkResult<T> {}
}

