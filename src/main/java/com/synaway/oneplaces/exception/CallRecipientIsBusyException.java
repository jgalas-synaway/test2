package com.synaway.oneplaces.exception;

public class CallRecipientIsBusyException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private static final String INFO = "Call recipient is busy: ";
    
    public CallRecipientIsBusyException() {
        super(INFO);
    }
    public CallRecipientIsBusyException(String message, Throwable cause) {
        super(INFO + message, cause);
    }
    public CallRecipientIsBusyException(String message) {
        super(INFO + message);
    }
    public CallRecipientIsBusyException(Throwable cause) {
        super(cause);
    }
}
