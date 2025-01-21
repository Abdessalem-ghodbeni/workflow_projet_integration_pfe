package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Repository.ITuteurRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IUserRepository;

import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IAuthenticationServices;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IJWTServices;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Service
@Slf4j
@RequiredArgsConstructor
public class IAuthenticationServicesImp implements IAuthenticationServices {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final IJWTServices jwtServices;
    private final ITuteurRepository tuteurRepository;

    public String generateQRCodeForUser(User user) throws QrGenerationException {
        String secret = new DefaultSecretGenerator().generate();
        user.setSecret(secret);
        // Save the user's secret to the database

        QrData data = new QrData.Builder()
                .label(user.getEmail())
                .secret(secret)
                .issuer("MyApp")
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        QrGenerator generator = new ZxingPngQrGenerator();
        byte[] imageData = generator.generate(data);
        return getDataUriForImage(imageData, generator.getImageMimeType());
    }

    @Override
    public Tuteur RegisterInstructor(Tuteur tuteur) {
        if (userRepository.findByEmail(tuteur.getEmail()).isPresent()) {
            throw new IllegalArgumentException("un tuteur avec cet email existe déja");
        }

        if (userRepository.findByIdentifiantEsprit(tuteur.getIdentifiantEsprit()).isPresent()) {
            throw new IllegalArgumentException("un tuteur  existe deja avec l'identifiant esprit ");
        }
        tuteur.setPassword(passwordEncoder.encode(tuteur.getPassword()));
        return tuteurRepository.save(tuteur);
    }

    @Override
    public AuthenticationResponse login(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        var user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        var jwt = jwtServices.generateToken(user);
        var refreshToken = jwtServices.generateRefreshToken(new HashMap<>(), user);

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();

        authenticationResponse.setAccessToken(jwt);
        authenticationResponse.setRefreshToken(refreshToken);

        if (user.getRole() == Role.TUTEUR) {
            Tuteur tuteur = (Tuteur) user;
            Tuteur tuteurDto = convertToInstructorDto(tuteur);
            authenticationResponse.setUserDetails(tuteurDto);
        }
//        else if (user.getRole()==Role.CLIENT) {
//            Client client = (Client) user;
//            Client agentDto = convertToClientDto(client);
//            authenticationResponse.setUserDetails(agentDto);
//        }
        else {
            User userDetails = convertToUserDto(user);
            authenticationResponse.setUserDetails(userDetails);
        }

        return authenticationResponse;
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshToken) {
        String userEmail = jwtServices.extractUsername(refreshToken.getRefreshToken());
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        if(jwtServices.isTokenValid(refreshToken.getRefreshToken(), user)) {
            var jwt = jwtServices.generateToken(user);

            AuthenticationResponse authenticationResponse = new AuthenticationResponse();

            authenticationResponse.setAccessToken(jwt);
            authenticationResponse.setRefreshToken(refreshToken.getRefreshToken());
            return authenticationResponse;
        }
        return null;
    }

    @Override
    public HashMap<String, String> forgetPassword(String email) {
        return null;
    }

    @Override
    public HashMap<String, String> resetPassword(String passwordResetToken, String newPassword) {
        return null;
    }




    private User convertToUserDto(User user) {
        User dto = new User();
        dto.setId(user.getId());
        dto.setNom(user.getNom());
        dto.setPrenom(user.getPrenom());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRole());
        dto.setNumeroTelephone(user.getNumeroTelephone());
    return dto;
    }
    private Tuteur convertToInstructorDto(Tuteur tuteur) {
        Tuteur dto = new Tuteur();
        dto.setId(tuteur.getId());
        dto.setNom(tuteur.getNom());
        dto.setPrenom(tuteur.getPrenom());
        dto.setEmail(tuteur.getEmail());
        dto.setPassword(tuteur.getPassword());
        dto.setRole(tuteur.getRole());
     dto.setSpecialiteUp(tuteur.getSpecialiteUp());
        dto.setNumeroTelephone(tuteur.getNumeroTelephone());
        dto.setDateEmbauche(tuteur.getDateEmbauche());
        dto.setImage(tuteur.getImage());
        dto.setNationality(tuteur.getNationality());
        return dto;
    }

// modifier account user
public User updateUser(Long userId, User updatedUser, MultipartFile image) throws IOException {
    // Rechercher l'utilisateur existant
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

    // Mettre à jour les informations
    if (updatedUser.getNom() != null) user.setNom(updatedUser.getNom());
    if (updatedUser.getPrenom() != null) user.setPrenom(updatedUser.getPrenom());
    if (updatedUser.getEmail() != null) user.setEmail(updatedUser.getEmail());
    if (updatedUser.getNumeroTelephone() != null) user.setNumeroTelephone(updatedUser.getNumeroTelephone());

    // Gestion de l'image (si fournie)
    if (image != null && !image.isEmpty()) {
        String originalFilename = image.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path filePath = Paths.get("upload-directory", uniqueFilename);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, image.getBytes());
        if (user instanceof Tuteur) {
            ((Tuteur) user).setImage(uniqueFilename);
        }
    }

    // Sauvegarder l'utilisateur mis à jour
    return userRepository.save(user);
}



}
