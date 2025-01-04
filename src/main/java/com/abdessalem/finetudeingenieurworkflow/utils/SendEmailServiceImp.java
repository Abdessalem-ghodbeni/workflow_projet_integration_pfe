package com.abdessalem.finetudeingenieurworkflow.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SendEmailServiceImp {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmailId;



    public void sendInstructorEmail(String recipient, String nomInstructor, String identifiantUnique, String cin) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(fromEmailId);
            helper.setTo(recipient);
            helper.setSubject("Invitation à rejoindre notre plateforme");

            // Préparer le contexte pour Thymeleaf
            Context context = new Context();
            context.setVariable("nomInstructor", nomInstructor);
            context.setVariable("identifiantUnique", identifiantUnique);
            context.setVariable("cin", cin);

            // Charger le contenu HTML
            String htmlContent = templateEngine.process("email-template", context);

            // Ajouter le contenu HTML à l'email
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
