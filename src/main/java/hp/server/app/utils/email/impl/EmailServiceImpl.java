package hp.server.app.utils.email.impl;

import hp.server.app.utils.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Autowired
    private JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    private String emailFrom;

    @Override
    public void sendEmail(String to, String subject, String body) {
        logger.info("Enter to sendEmail()");
        logger.info("----- Send email to: " + to + " -----");

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            emailSender.send(message);
        } catch (MailException e) {
            logger.error("An error occurred when trying to send the email");
            e.printStackTrace();
        }
    }
}
