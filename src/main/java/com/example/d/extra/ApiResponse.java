package com.example.d.extra;

public class ApiResponse {
    private String message;
    private boolean status;
    private Object data;

    public ApiResponse() {
    }

    public ApiResponse(String message, boolean status) {
        this.message = message;
        this.status = status;
    }

    public ApiResponse(String message, boolean status, Object data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

    private ApiResponse(Builder builder) {
        this.message = builder.message;
        this.status = builder.status;
        this.data = builder.data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "message='" + message + '\'' +
                ", status=" + status +
                ", data=" + data +
                '}';
    }

    public static class Builder {
        private String message;
        private boolean status;
        private Object data;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder status(boolean status) {
            this.status = status;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public ApiResponse build() {
            return new ApiResponse(this);
        }
    }
}