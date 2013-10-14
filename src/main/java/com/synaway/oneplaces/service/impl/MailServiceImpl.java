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

    /**
     * service for email sending
     */
    @Autowired
    private MailSender mailSender;

    /**
     * default value for setting "from" address in sending message
     */
    @Value("${mail.default.from}")
    private String defaultAddressFrom;

    /**
     * content for email message send to notify about new release of mobile
     * application
     */
    @Value("${mail.template.mobileRelease.message}")
    private String templateMobileAppReleaseInfo;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMail(String addressTo, String subject, String body) {
        sendMail(new EmailNotification(addressTo, subject, body));
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
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
