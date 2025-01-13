package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.User;
import com.abdessalem.finetudeingenieurworkflow.Repository.IUserRepository;
import com.abdessalem.finetudeingenieurworkflow.utils.SendEmailServiceImp;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final IUserRepository userRepository;
    private final SendEmailServiceImp sendEmailServiceImp;

    @Value("${spring.mail.username}")
    private String fromEmailId;

    public void createPasswordResetTokenForUser(User user, String token) {
        user.setPasswordResetToken(token);
        userRepository.save(user);
    }

    public void sendPasswordResetEmail(String email, String token) {
        sendEmailServiceImp.sendPasswordResetEmail(email, token);
    }

    public void resetPassword(String email, String newPassword, String token) {
        // Trouver l'utilisateur par email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        // Vérifier si le token correspond
        if (!token.equals(user.getPasswordResetToken())) {
            throw new IllegalArgumentException("Token invalide");
        }

        // Vérifier si le token a expiré
        if (user.getPasswordResetTokenExpiration() != null &&
                user.getPasswordResetTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expiré");
        }

        // Hacher et enregistrer le nouveau mot de passe
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);

        // Réinitialiser le token de réinitialisation
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiration(null);
        userRepository.save(user);
    }

}

