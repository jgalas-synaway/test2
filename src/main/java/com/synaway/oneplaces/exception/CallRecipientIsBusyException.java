package com.synaway.oneplaces.exception;

public class CallRecipientIsBusyException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private static final String info = "Call recipient is busy: ";
    
    public CallRecipientIsBusyException() {
        super(info);
    }
    public CallRecipientIsBusyException(String message, Throwable cause) {
        super(info + message, cause);
    }
    public CallRecipientIsBusyException(String message) {
        super(info + message);
    }
    public CallRecipientIsBusyException(Throwable cause) {
        super(cause);
    }
}
