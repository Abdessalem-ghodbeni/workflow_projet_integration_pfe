package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Repository.IInstructorRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IUserRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IAuthenticationServices;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.AuthenticatorService;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.IJWTServicesImp;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.PasswordResetService;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.TwoFactorAuthenticationService;
import com.abdessalem.finetudeingenieurworkflow.utils.SendEmailServiceImp;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

  public static String uploadDirectory = System.getProperty("user.dir") + "/uploadUser";

  private final IAuthenticationServices authenticationServices;
  private final SendEmailServiceImp sendEmailService;
  private final IUserRepository userRepository;
  private final IInstructorRepository instructorRepository;
  private final TwoFactorAuthenticationService tfaService;
  private final PasswordEncoder passwordEncoder;
  private final PasswordResetService passwordResetService;
  private final AuthenticatorService authenticatorService;
  private final IJWTServicesImp jwtServices;

  @PostMapping("/registerInstructor")
  public ResponseEntity<Instructor> registerInstructor(@RequestParam("nom") String nom,
                                             @RequestParam("prenom") String prenom,
                                             @RequestParam("email") String email,
                                             @RequestParam("password") String password,
                                             @RequestParam("numeroTelephone") String numeroTelephone,
                                             @RequestParam("identifiantEsprit") String identifiantEsprit,
                                             @RequestParam("specialiteUp") String specialiteUp,
                                             @RequestParam("nationality") String nationality,
                                             @RequestParam("is_Chef_Options") boolean is_Chef_Options,
                                             @RequestParam("dateEmbauche") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateEmbauche,
                                             @RequestParam("image") MultipartFile file) throws IOException {
    Instructor instructor = new Instructor();
    instructor.setNom(nom);
    instructor.setPrenom(prenom);
    instructor.setEmail(email);
    instructor.setPassword(password);
    instructor.setIdentifiantEsprit(identifiantEsprit);
    instructor.setNumeroTelephone(numeroTelephone);
    instructor.setDateEmbauche(dateEmbauche);
    instructor.setRole(Role.INSTRUCTOR);
    instructor.setSpecialiteUp(specialiteUp);
    instructor.setNationality(nationality);
    String originalFilename = file.getOriginalFilename();
    String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
    Path fileNameAndPath = Paths.get(uploadDirectory, uniqueFilename);
    if (!Files.exists(fileNameAndPath.getParent())) {
       Files.createDirectories(fileNameAndPath.getParent());
  }
  Files.write(fileNameAndPath, file.getBytes());
    instructor.setImage(uniqueFilename);
    Instructor savedInstructor = authenticationServices.RegisterInstructor(instructor);
    if (savedInstructor != null) {
      String identifiantUnique = savedInstructor.getIdentifiantEsprit();
      String cin = savedInstructor.getNumeroTelephone(); // Remplacez par la variable CIN correcte
      sendEmailService.sendInstructorEmail(email, nom + " " + prenom, identifiantUnique, cin);
    }
    return ResponseEntity.ok(savedInstructor);
  }

  @GetMapping("/{filename:.+}")
  @ResponseBody
  public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws MalformedURLException {

    Path filePath = Paths.get(uploadDirectory).resolve(filename);
    Resource file = new UrlResource(filePath.toUri());

    if (file.exists() || file.isReadable()) {
      return ResponseEntity
              .ok()
              .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
              .body(file);
    } else {
      throw new RuntimeException("Could not read the file!");
    }
  }

  @PostMapping("/login")
  public AuthenticationResponse login(@RequestBody User user) {
      return authenticationServices.login(user.getEmail(), user.getPassword());
  }

  @PostMapping("/refreshToken")
  public AuthenticationResponse refreshToken(@RequestBody RefreshTokenRequest refreshToken) {
      return authenticationServices.refreshToken(refreshToken);
  }

//  @PostMapping("/forgetpassword")
//  public HashMap<String,String> forgetPassword(@RequestBody String email){
//        return authenticationServices.forgetPassword(email);
//  }

//    @PostMapping("/resetPassword/{passwordResetToken}")
//    public HashMap<String,String> resetPassword(@PathVariable String passwordResetToken, String newPassword){
//        return authenticationServices.resetPassword(passwordResetToken, newPassword);
//    }
//////////////
@PostMapping("/forgot-password")
public ResponseEntity<String> forgotPassword(@RequestParam String email) {
  Optional<User> userOptional = userRepository.findByEmail(email); if (userOptional.isPresent()) { String token = UUID.randomUUID().toString(); User user = userOptional.get(); passwordResetService.createPasswordResetTokenForUser(user, token); passwordResetService.sendPasswordResetEmail(email, token); return ResponseEntity.ok("Email de réinitialisation envoyé!"); } else { return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé"); }
}

//  @PostMapping("/validate-otp")
//  public ResponseEntity<String> validateOtp(@RequestParam String email, @RequestParam String code) {
//    User user = userRepository.findByEmail(email)
//            .orElseThrow(() -> new EntityNotFoundException("User not found"));
//
//    if (!tfaService.isOtpValid(user.getSecret(), code)) {
//      return ResponseEntity.badRequest().body("Invalid OTP code");
//    }
//
//    // Redirect to reset password
//    return ResponseEntity.ok("OTP verified. Redirecting to reset password...");
//  }

  @PostMapping("/verify-otp")
  public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody VerificationRequest verificationRequest) {
    String email = verificationRequest.getEmail();
    Integer code = verificationRequest.getCode();
    log.info("sehla" ,email);

    // Vérifiez si les paramètres nécessaires sont présents
    if (email == null || code == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(Collections.singletonMap("message", "Les paramètres 'email' et 'code' sont requis"));
    }

    log.info("Vérification de l'utilisateur avec l'email : {}", email);

    Optional<User> userOptional = userRepository.findByEmail(email);
    if (userOptional.isEmpty()) {
      log.warn("Utilisateur non trouvé : {}", email);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(Collections.singletonMap("message", "Utilisateur non trouvé"));
    }

    User user = userOptional.get();
    log.info("Utilisateur trouvé : {}", user.getEmail());

    // Vérifiez si le code OTP est valide
    boolean isCodeValid = authenticatorService.verifyCode(user.getSecret(), code);
    if (!isCodeValid) {
      log.warn("Code OTP invalide pour l'utilisateur : {}", email);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(Collections.singletonMap("message", "Code OTP invalide ou expiré"));
    }

    // Génération du token JWT
    String token = jwtServices.generateToken(user);
    Map<String, Object> response = new HashMap<>();
    response.put("token", token);
    response.put("user", user);

    log.info("Authentification réussie pour l'utilisateur : {}", email);
    return ResponseEntity.ok(response);
  }











  @GetMapping("/reset-password")
  public ResponseEntity<String> showResetPasswordPage(@RequestParam String token) {
    Optional<User> userOptional = userRepository.findByPasswordResetToken(token);
    if (userOptional.isPresent()) {
      User user = userOptional.get();
      if (user.getSecret() == null) {
        user.setSecret(generateSecret());
        userRepository.save(user);
      }
      String qrCodeUrl = authenticatorService.generateQRCode(user.getSecret(), user.getEmail());
      log.info("QR Code URL: " + qrCodeUrl);
      return ResponseEntity.ok("<html><body><img src=\"" + qrCodeUrl + "\"></body></html>");
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Jeton invalide");
    }
  }

  public String generateSecret() {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[10];
    random.nextBytes(bytes);
    Base32 base32 = new Base32();
    return base32.encodeToString(bytes);
  }


  @PostMapping("/update-password")
  public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest) {
    String email = passwordResetRequest.getEmail();
    String newPassword = passwordResetRequest.getNewPassword();
    String token = passwordResetRequest.getToken();

    if (email == null || newPassword == null || token == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(Collections.singletonMap("message", "Les champs 'email', 'nouveau mot de passe' et 'token' sont requis"));
    }

    try {
      passwordResetService.resetPassword(email, newPassword, token);
      return ResponseEntity.ok(Collections.singletonMap("message", "Mot de passe réinitialisé avec succès"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(Collections.singletonMap("message", e.getMessage()));
    }
  }

}
