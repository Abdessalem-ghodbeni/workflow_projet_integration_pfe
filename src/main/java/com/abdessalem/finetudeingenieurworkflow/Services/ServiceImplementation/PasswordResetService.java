package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.User;
import com.abdessalem.finetudeingenieurworkflow.Repository.IUserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final IUserRepository userRepository;

    @Value("${spring.mail.username}")
    private String fromEmailId;

    public void sendPasswordResetLink(String recipientEmail) {
        // Trouver l'utilisateur par e-mail
        User user = userRepository.findByEmail(recipientEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Générer un token de réinitialisation
        String resetToken = generateResetToken();
        user.setPasswordResetToken(resetToken);
        userRepository.save(user);

        // Créer un lien de réinitialisation
        String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;

        // Envoyer l'email avec le lien de réinitialisation
        sendResetEmail(recipientEmail, resetLink);
    }

    private void sendResetEmail(String recipient, String resetLink) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(fromEmailId);
            helper.setTo(recipient);
            helper.setSubject("Réinitialisation du mot de passe");

            // Préparer le contexte pour Thymeleaf
            Context context = new Context();
            context.setVariable("resetLink", resetLink);

            // Charger le contenu HTML pour le template de réinitialisation
            String htmlContent = templateEngine.process("reset-password-template", context);

            // Ajouter le contenu HTML à l'email
            helper.setText(htmlContent, true);

            // Envoyer l'e-mail
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'e-mail", e);
        }
    }

    // Méthode pour générer un token de réinitialisation
    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }
}
