package com.synaway.oneplaces.exception;

public class UserException extends GeneralException {

    private static final long serialVersionUID = 1L;
    
    private static final String info = "AccessToken exception";
    
    public UserException() {
        super(info);
        this.code = GENERAL_ACCESS_TOKEN_EXCEPTION;
    }
    public UserException(String message, Throwable cause) {
        super(message, cause);
        this.code = GENERAL_ACCESS_TOKEN_EXCEPTION;
    }
    public UserException(String message) {
        super( message);
        this.code = GENERAL_ACCESS_TOKEN_EXCEPTION;
    }
    public UserException(Throwable cause) {
        super(cause);
        this.code = GENERAL_ACCESS_TOKEN_EXCEPTION;
    }
    public UserException(String message, int code) {
        super( message);
        this.code = code;
    }
}
