package common;

public enum MessageType {
    LOGIN,
    AUTH_SUCCESS,
    AUTH_FAIL,

    // Server -> Client
    START_WATCH,
    STOP_WATCH,

    // Client -> Server
    FILE_EVENT,

    // Nhóm tiện ích
    ERROR
}