package com.synaway.oneplaces.exception;

public class ObjectByIdNotFoundException extends GeneralException {

    private static final long serialVersionUID = 1L;

    
    private static final String INFO = "Not found object with given id";
    
    
    public ObjectByIdNotFoundException() {
        super(INFO);
        this.setCode(OBJECT_NOT_FOUND);
    }
    public ObjectByIdNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.setCode(OBJECT_NOT_FOUND);
    }
    public ObjectByIdNotFoundException(String message) {
        super(message);
        this.setCode(OBJECT_NOT_FOUND);
    }
    public ObjectByIdNotFoundException(Throwable cause) {
        super(cause);
        this.setCode(OBJECT_NOT_FOUND);
    }
    public ObjectByIdNotFoundException(String message, int code) {
        super(message, code);
        this.setCode(code);
    }
}
