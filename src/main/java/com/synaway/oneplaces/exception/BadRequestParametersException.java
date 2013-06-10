package com.synaway.oneplaces.exception;

public class BadRequestParametersException extends GeneralException {

    private static final long serialVersionUID = 1L;
    
    private static final String INFO = "Bad request parameters:";
    
    public BadRequestParametersException() {
        super(INFO);
        this.setCode(GENERAL_FILE_EXCEPTION);
    }
    public BadRequestParametersException(String message, Throwable cause) {
        super(message, cause);
        this.setCode(GENERAL_FILE_EXCEPTION);
    }
    public BadRequestParametersException(String message) {
        super( message);
        this.setCode(GENERAL_FILE_EXCEPTION);
    }
    public BadRequestParametersException(Throwable cause) {
        super(cause);
        this.setCode(GENERAL_FILE_EXCEPTION);
    }
    public BadRequestParametersException(String message, int code) {
        super( message);
        this.setCode(code);
    }
}
