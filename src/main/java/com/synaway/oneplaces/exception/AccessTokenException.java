package com.synaway.oneplaces.exception;

public class AccessTokenException extends GeneralException {

    private static final long serialVersionUID = 1L;
    
    private static final String INFO = "AccessToken exception";
    
    public AccessTokenException() {
        super(INFO);
        this.setCode(GENERAL_EXCEPTION);
    }
    public AccessTokenException(String message, Throwable cause) {
        super(message, cause);
        this.setCode(GENERAL_EXCEPTION);
    }
    public AccessTokenException(String message) {
        super( message);
        this.setCode(GENERAL_EXCEPTION);
    }
    public AccessTokenException(Throwable cause) {
        super(cause);
        this.setCode(GENERAL_EXCEPTION);
    }
    public AccessTokenException(String message, int code) {
        super( message);
        this.setCode(code);
    }
}
