package com.synaway.oneplaces.exception;

public class UserException extends GeneralException {

    private static final long serialVersionUID = 1L;
    
    private static final String INFO = "AccessToken exception";
    
    public UserException() {
        super(INFO);
        this.setCode(GENERAL_ACCESS_TOKEN_EXCEPTION);
    }
    public UserException(String message, Throwable cause) {
        super(message, cause);
        this.setCode(GENERAL_ACCESS_TOKEN_EXCEPTION);
    }
    public UserException(String message) {
        super( message);
        this.setCode(GENERAL_ACCESS_TOKEN_EXCEPTION);
    }
    public UserException(Throwable cause) {
        super(cause);
        this.setCode(GENERAL_ACCESS_TOKEN_EXCEPTION);
    }
    public UserException(String message, int code) {
        super( message);
        this.setCode(code);
    }
}
