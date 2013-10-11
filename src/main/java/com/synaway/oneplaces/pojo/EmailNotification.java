package com.synaway.oneplaces.pojo;

import org.springframework.web.multipart.MultipartFile;

public class EmailNotification {
    private String addressTo;
    private String addressFrom;
    private String subject;
    private String body;
    private MultipartFile attachment;
    
    public EmailNotification(String addressTo, String addressFrom, String subject, String body) {
        this.addressTo = addressTo;
        this.addressFrom = addressFrom;
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

    public MultipartFile getAttachment() {
        return attachment;
    }

    public void setAttachment(MultipartFile attachment) {
        this.attachment = attachment;
    }
}
