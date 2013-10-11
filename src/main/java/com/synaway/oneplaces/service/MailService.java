package com.synaway.oneplaces.service;

import com.synaway.oneplaces.exception.UserException;
import com.synaway.oneplaces.pojo.EmailNotification;

public interface MailService {

    public void sendMail(String addressTo, String subject, String body);

    public void sendMail(EmailNotification en);

    public void sendMobileAppReleaseMail(Long userId, String mobileAppUrl) throws UserException;
}
