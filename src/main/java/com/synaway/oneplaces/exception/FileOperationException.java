package com.synaway.oneplaces.exception;

public class FileOperationException extends GeneralException {

    private static final long serialVersionUID = 1L;
    
    private static final String INFO = "File operation exception:";
    
    public FileOperationException() {
        super(INFO);
        this.setCode(GENERAL_FILE_EXCEPTION);
    }
    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
        this.setCode(GENERAL_FILE_EXCEPTION);
    }
    public FileOperationException(String message) {
        super( message);
        this.setCode(GENERAL_FILE_EXCEPTION);
    }
    public FileOperationException(Throwable cause) {
        super(cause);
        this.setCode(GENERAL_FILE_EXCEPTION);
    }
    public FileOperationException(String message, int code) {
        super( message);
        this.setCode(code);
    }
}
