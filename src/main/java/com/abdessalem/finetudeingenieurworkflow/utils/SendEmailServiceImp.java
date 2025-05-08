package com.abdessalem.finetudeingenieurworkflow.utils;

import com.abdessalem.finetudeingenieurworkflow.Entites.Tache;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Year;


@Service
@Slf4j
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
    ////
    public void sendPasswordResetEmail(String email, String resetLink) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(fromEmailId);
            helper.setTo(email);
            helper.setSubject("Password Reset Request");

            Context context = new Context();
            context.setVariable("resetLink", resetLink);
            String htmlContent = templateEngine.process("password-reset-template", context);

            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }




    public void sendSocieteEmail(String recipient, String nomSociete, String password ) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(fromEmailId);
            helper.setTo(recipient);
            helper.setSubject("Invitation à rejoindre notre plateforme");

            // Préparer le contexte pour Thymeleaf
            Context context = new Context();
            context.setVariable("nomSociete", nomSociete);
            context.setVariable("password", password);
            context.setVariable("recipient", recipient);

            // Charger le contenu HTML
            String htmlContent = templateEngine.process("email-societe-invitation.html", context);

            // Ajouter le contenu HTML à l'email
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }


    public void sendTaskOverdueEmail(String studentEmail, String tutorEmail, Tache task) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmailId);
            helper.setTo(studentEmail);
            helper.addCc(tutorEmail);
            helper.setSubject("[URGENT] Retard sur la tâche : " + task.getTitre());

            Context context = new Context();
            context.setVariable("task", task);
            context.setVariable("student", task.getEtudiant());
            context.setVariable("tutor", task.getEtudiant().getEquipe().getTuteur());
            context.setVariable("currentYear", Year.now().getValue());

            String htmlContent = templateEngine.process("overdue-task-template.html", context);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("Email envoyé pour la tâche {} à {}", task.getId(), studentEmail);
        } catch (MessagingException e) {
            log.error("Échec d'envoi d'email pour la tâche {} : {}", task.getId(), e.getMessage());
            throw new RuntimeException("Erreur d'envoi d'email", e);
        }
    }




}
