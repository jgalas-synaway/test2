package com.synaway.oneplaces.exception;

public class CallConnectionAlreadyClosedException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private static final String INFO = "Call connection was already closed: ";
    
    public CallConnectionAlreadyClosedException() {
        super(INFO);
    }
    public CallConnectionAlreadyClosedException(String message, Throwable cause) {
        super(INFO + message, cause);
    }
    public CallConnectionAlreadyClosedException(String message) {
        super(INFO + message);
    }
    public CallConnectionAlreadyClosedException(Throwable cause) {
        super(cause);
    }
}
