package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;



import com.abdessalem.finetudeingenieurworkflow.Entites.AuthenticationResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Instructor;
import com.abdessalem.finetudeingenieurworkflow.Entites.RefreshTokenRequest;

import java.util.HashMap;

public interface IAuthenticationServices {

    AuthenticationResponse login(String email, String password);
    AuthenticationResponse refreshToken(RefreshTokenRequest refreshToken);
    HashMap<String,String> forgetPassword(String email);
    HashMap<String,String> resetPassword(String passwordResetToken, String newPassword);
//    Instructor addInstructor(Instructor Instructor);
    Instructor RegisterInstructor(Instructor instructor);
}
