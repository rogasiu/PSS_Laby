package pl.pss.PSS.mailer;

public interface EmailSender {
    void sendEmail(String to, String subject, String content);
}