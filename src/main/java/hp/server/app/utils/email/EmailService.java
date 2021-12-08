package hp.server.app.utils.email;

public interface EmailService {

    public void sendEmail(String to, String subject, String body);
}
