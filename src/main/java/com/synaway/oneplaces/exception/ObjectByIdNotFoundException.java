package com.synaway.oneplaces.exception;

public class ObjectByIdNotFoundException extends GeneralException {

    private static final long serialVersionUID = 1L;

    
    private static final String info = "Not found object with given id";
    
    
    public ObjectByIdNotFoundException() {
        super(info);
        this.code = OBJECT_NOT_FOUND;
    }
    public ObjectByIdNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.code = OBJECT_NOT_FOUND;
    }
    public ObjectByIdNotFoundException(String message) {
        super(message);
        this.code = OBJECT_NOT_FOUND;
    }
    public ObjectByIdNotFoundException(Throwable cause) {
        super(cause);
        this.code = OBJECT_NOT_FOUND;
    }
    public ObjectByIdNotFoundException(String message, int code) {
        super(message, code);
        this.code = code;
    }
}
