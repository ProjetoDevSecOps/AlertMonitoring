package br.com.alertmonitoring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.email.to}")
    private String toEmail;

    public void sendAlertEmail(String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(toEmail);
            mailMessage.setSubject("⚠️ Alerta de Monitoramento");
            mailMessage.setText(message);
            
            mailSender.send(mailMessage);
            logger.info("Alerta enviado com sucesso para {}.", toEmail);
        } catch (Exception e) {
            logger.error("Erro ao enviar o e-mail: {}", e.getMessage());
        }
    }
}
