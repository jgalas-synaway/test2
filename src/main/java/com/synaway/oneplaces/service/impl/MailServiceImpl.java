package com.synaway.oneplaces.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synaway.oneplaces.exception.UserException;
import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.pojo.EmailNotification;
import com.synaway.oneplaces.repository.UserRepository;
import com.synaway.oneplaces.service.MailService;

@Service
@Transactional
public class MailServiceImpl implements MailService {

    @Autowired
    private MailSender mailSender;

    @Value("${mail.default.from}")
    private String defaultAddressFrom;

    @Value("${mail.template.mobileRelease.message}")
    private String templateMobileAppReleaseInfo;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Send simpleMailMessage with given parameters: address to, subject, body
     * 
     * @param addressTo email address where we should send email
     * @param subject email subject
     * @param body email content
     */
    @Override
    public void sendMail(String addressTo, String subject, String body) {
        sendMail(new EmailNotification(addressTo, subject, body));
    }

    /**
     * sending email with defined in emailNotification data: address to,
     * address from, address reply to, subject, body
     * 
     * @param en
     */
    @Override
    public void sendMail(EmailNotification en) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(en.getAddressTo());
        message.setSubject(en.getSubject());
        message.setText(en.getBody());
        message.setFrom(en.getAddressFrom());
        message.setReplyTo(en.getAddressReplyTo());
        mailSender.send(message);
    }

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
    @Override
    public void sendMobileAppReleaseMail(Long userId, String mobileAppUrl) throws UserException {
        User user = userRepository.findOne(userId);
        if (user == null) {
            throw new UserException("User not found", UserException.USER_NOT_FOUND);
        }
        
        String body = String.format(templateMobileAppReleaseInfo, mobileAppUrl);
        sendMail(user.getEmail(), "Mobile application release", body);
    }

}
