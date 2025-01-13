package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.User;
import com.abdessalem.finetudeingenieurworkflow.Repository.IUserRepository;
import com.abdessalem.finetudeingenieurworkflow.utils.SendEmailServiceImp;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;


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
}

