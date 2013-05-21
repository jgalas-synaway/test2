package com.synaway.oneplaces.exception;

public class CallConnectionAlreadyClosedException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private static final String info = "Call connection was already closed: ";
    
    public CallConnectionAlreadyClosedException() {
        super(info);
    }
    public CallConnectionAlreadyClosedException(String message, Throwable cause) {
        super(info + message, cause);
    }
    public CallConnectionAlreadyClosedException(String message) {
        super(info + message);
    }
    public CallConnectionAlreadyClosedException(Throwable cause) {
        super(cause);
    }
}
