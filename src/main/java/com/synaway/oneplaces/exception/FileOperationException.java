package com.synaway.oneplaces.exception;

public class FileOperationException extends GeneralException {

    private static final long serialVersionUID = 1L;
    
    private static final String info = "File operation exception:";
    
    public FileOperationException() {
        super(info);
        this.code = GENERAL_FILE_EXCEPTION;
    }
    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
        this.code = GENERAL_FILE_EXCEPTION;
    }
    public FileOperationException(String message) {
        super( message);
        this.code = GENERAL_FILE_EXCEPTION;
    }
    public FileOperationException(Throwable cause) {
        super(cause);
        this.code = GENERAL_FILE_EXCEPTION;
    }
    public FileOperationException(String message, int code) {
        super( message);
        this.code = code;
    }
}
