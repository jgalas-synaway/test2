package com.synaway.oneplaces.service;

import com.synaway.oneplaces.exception.UserException;
import com.synaway.oneplaces.pojo.EmailNotification;

public interface MailService {

    /**
     * Send simpleMailMessage with given parameters: address to, subject, body
     * 
     * @param addressTo email address where we should send email
     * @param subject email subject
     * @param body email content
     */
    void sendMail(String addressTo, String subject, String body);

    /**
     * sending email with defined in emailNotification data: address to,
     * address from, address reply to, subject, body
     * 
     * @param en
     */
    void sendMail(EmailNotification en);

    /**
     * Send email with information about release of certain version of 1places
     * mobile application. Email content and mobile application url is set on
     * default values (set in property file).
     * 
     * @param userId
     *            id of user for which we want to send and email
     * @param mobileAppUrl full string to url from where mobile application can be download
     * @throws UserException when user with given id not exist
     */
    void sendMobileAppReleaseMail(Long userId, String mobileAppUrl) throws UserException;
}
