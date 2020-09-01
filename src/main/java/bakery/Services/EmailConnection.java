package bakery.Services;

import java.util.Properties;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
class EmailConnection {

    private static final JavaMailSender sender = getJavaMailSender();
    private final String BASE_URL = "http://localhost:3000/Verified/";
    private final String VERIFICATION = "Verification Token";

    private EmailConnection() {
    }

    void sendEmailToken(String token, String receiver) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(receiver);
        msg.setSubject(VERIFICATION);
        msg.setText(BASE_URL + token);

        sender.send(msg);
    }

    private static JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("kikisbakerydelivery@gmail.com");
        mailSender.setPassword("Justin!1230");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        return mailSender;
    }

}