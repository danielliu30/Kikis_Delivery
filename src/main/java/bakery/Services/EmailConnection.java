package bakery.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
class EmailConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailConnection.class);
    private final String BASE_URL = "http://localhost:3000/Verified/";
    private final String VERIFICATION = "Verification Token";

    private final JavaMailSender sender;

    @Autowired
    EmailConnection(JavaMailSender sender) {
        this.sender = sender;
    }

    void sendEmailToken(String token, String receiver) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(receiver);
        msg.setSubject(VERIFICATION);
        msg.setText(BASE_URL + token);

        try {
            sender.send(msg);
        } catch (MailSendException e) {
            LOGGER.error("Failed to send email. Email Config may be invalid", e);
        }
    }
}