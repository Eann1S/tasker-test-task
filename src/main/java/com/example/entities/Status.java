package com.example.entities;

public enum Status {
    TODO, IN_PROGRESS, DONE;

    public static Status from(String status) {
        status = status.toUpperCase();
        return Status.valueOf(status);
    }
}
