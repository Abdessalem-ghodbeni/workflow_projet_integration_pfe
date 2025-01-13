package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticatorService {
    private final CustomCredentialRepository customCredentialRepository;

    public String generateQRCode(String secret, String email) {
        // Configuration pour changer la période à 60 secondes
        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setTimeStepSizeInMillis(60000) // 60 seconds
                .setWindowSize(3)
                .build();

        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator(config);
        googleAuthenticator.setCredentialRepository(customCredentialRepository);
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials(email);

        log.info("Generated OTPAuth URL: {}", GoogleAuthenticatorQRGenerator.getOtpAuthURL("FinEtudeIngenieurWorkflow", email, key));
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("FinEtudeIngenieurWorkflow", email, key);
    }
    public boolean verifyCode(String secret, int code) {
        log.info("Vérification du code OTP avec la clé : {}, code : {}", secret, code);

        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setTimeStepSizeInMillis(60000) // 30 secondes
                .setWindowSize(5) // Tolérance pour 5 intervalles
                .build();

        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator(config);
        return googleAuthenticator.authorize(secret, code);
    }
}
