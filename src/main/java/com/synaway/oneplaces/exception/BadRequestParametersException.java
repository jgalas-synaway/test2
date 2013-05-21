package com.synaway.oneplaces.exception;

public class BadRequestParametersException extends GeneralException {

    private static final long serialVersionUID = 1L;
    
    private static final String info = "Bad request parameters:";
    
    public BadRequestParametersException() {
        super(info);
        this.code = GENERAL_FILE_EXCEPTION;
    }
    public BadRequestParametersException(String message, Throwable cause) {
        super(message, cause);
        this.code = GENERAL_FILE_EXCEPTION;
    }
    public BadRequestParametersException(String message) {
        super( message);
        this.code = GENERAL_FILE_EXCEPTION;
    }
    public BadRequestParametersException(Throwable cause) {
        super(cause);
        this.code = GENERAL_FILE_EXCEPTION;
    }
    public BadRequestParametersException(String message, int code) {
        super( message);
        this.code = code;
    }
}
