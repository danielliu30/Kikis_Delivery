package bakery.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
class Email implements  {
    
    @Autowired
    private JavaMailSender sender;
    private final String BASE_URI = "localhost:8080/validateToken-";
    @Autowired
    private SimpleMailMessage message;

    void sendVerificationToken(String token, String email){
        message.setFrom("danielj.liu30@gmail.com");
        message.setTo(email);
        message.setSubject("ValidateAccount");
        message.setText(BASE_URI + token);
        sender.send(message);
    }
}