package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Repository.IInstructorRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IUserRepository;

import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IAuthenticationServices;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IJWTServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class IAuthenticationServicesImp implements IAuthenticationServices {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final IJWTServices jwtServices;
    private final IInstructorRepository instructorRepository;

    @Override
    public Instructor RegisterInstructor(Instructor instructor) {
        if (userRepository.findByEmail(instructor.getEmail()).isPresent()) {
            throw new IllegalArgumentException("un instructor avec cet email existe déja");
        }

        if (userRepository.findByIdentifiantEsprit(instructor.getIdentifiantEsprit()).isPresent()) {
            throw new IllegalArgumentException("un instructor existe deja avec l'identifiant esprit ");
        }
        instructor.setPassword(passwordEncoder.encode(instructor.getPassword()));
        return instructorRepository.save(instructor);
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

        if (user.getRole() == Role.INSTRUCTOR) {
            Instructor instructor = (Instructor) user;
            Instructor instructorDto = convertToInstructorDto(instructor);
            authenticationResponse.setUserDetails(instructorDto);
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
    private Instructor convertToInstructorDto(Instructor instructor) {
        Instructor dto = new Instructor();
        dto.setId(instructor.getId());
        dto.setNom(instructor.getNom());
        dto.setPrenom(instructor.getPrenom());
        dto.setEmail(instructor.getEmail());
        dto.setPassword(instructor.getPassword());
        dto.setRole(instructor.getRole());
     dto.setSpecialiteUp(instructor.getSpecialiteUp());
        dto.setNumeroTelephone(instructor.getNumeroTelephone());
        dto.setDateEmbauche(instructor.getDateEmbauche());
        dto.setImage(instructor.getImage());
        dto.setNationality(instructor.getNationality());
        return dto;
    }


}
