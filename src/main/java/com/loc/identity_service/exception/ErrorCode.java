package com.loc.identity_service.exception;

public enum ErrorCode {
    UNCATEGORIZED(9999, "Uncategorized error."),
    INVALID_KEY(8888, "Invalid message key."),
    USER_EXISTS(1001, "User already exists."),
    USERNAME_INVALID(1003, "Username must have at least 3 characters."),
    PASSWORD_INVALID(1004, "Password must have at least 8 characters."),
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;
    
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

}
