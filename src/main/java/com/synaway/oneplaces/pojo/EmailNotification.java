package com.synaway.oneplaces.pojo;

/**
 * Object containing typical data for sending mail.
 *
 */
public class EmailNotification {

    /*
     * email address where we want to send message
     */
    private String addressTo;

    /**
     * email address from we send message
     */
    private String addressFrom;

    /**
     * email address where should be send answer for current mail
     */
    private String addressReplyTo;

    /**
     * email subject
     */
    private String subject;

    /**
     * content of the email
     */
    private String body;

    public EmailNotification(String addressTo, String subject, String body) {
        this.addressTo = addressTo;
        this.subject = subject;
        this.body = body;
    }

    public String getAddressTo() {
        return addressTo;
    }

    public void setAddressTo(String addressTo) {
        this.addressTo = addressTo;
    }

    public String getAddressFrom() {
        return addressFrom;
    }

    public void setAddressFrom(String addressFrom) {
        this.addressFrom = addressFrom;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAddressReplyTo() {
        return addressReplyTo;
    }

    public void setAddressReplyTo(String addressReplyTo) {
        this.addressReplyTo = addressReplyTo;
    }
}
