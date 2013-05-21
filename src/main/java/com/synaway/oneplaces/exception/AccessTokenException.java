package com.synaway.oneplaces.exception;

public class AccessTokenException extends GeneralException {

    private static final long serialVersionUID = 1L;
    
    private static final String info = "AccessToken exception";
    
    public AccessTokenException() {
        super(info);
        this.code = GENERAL_ACCESS_TOKEN_EXCEPTION;
    }
    public AccessTokenException(String message, Throwable cause) {
        super(message, cause);
        this.code = GENERAL_ACCESS_TOKEN_EXCEPTION;
    }
    public AccessTokenException(String message) {
        super( message);
        this.code = GENERAL_ACCESS_TOKEN_EXCEPTION;
    }
    public AccessTokenException(Throwable cause) {
        super(cause);
        this.code = GENERAL_ACCESS_TOKEN_EXCEPTION;
    }
    public AccessTokenException(String message, int code) {
        super( message);
        this.code = code;
    }
}
