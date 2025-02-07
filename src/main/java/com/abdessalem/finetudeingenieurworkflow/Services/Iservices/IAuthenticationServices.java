package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;



import com.abdessalem.finetudeingenieurworkflow.Entites.*;

import java.util.HashMap;

public interface IAuthenticationServices {

    AuthenticationResponse login(String email, String password);
    AuthenticationResponse refreshToken(RefreshTokenRequest refreshToken);
    HashMap<String,String> forgetPassword(String email);
    HashMap<String,String> resetPassword(String passwordResetToken, String newPassword);
    Etudiant registerEtudiant(Etudiant etudiant);
    Tuteur RegisterInstructor(Tuteur tuteur);
    Societe RegisterSociete(Societe societe);
}
