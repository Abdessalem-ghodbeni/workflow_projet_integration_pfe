package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IAuthenticationServices;
import com.abdessalem.finetudeingenieurworkflow.utils.SendEmailServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  public static String uploadDirectory = System.getProperty("user.dir") + "/uploadUser";

  private final IAuthenticationServices authenticationServices;
  private final SendEmailServiceImp sendEmailService;

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

  @PostMapping("/forgetpassword")
  public HashMap<String,String> forgetPassword(@RequestBody String email){
        return authenticationServices.forgetPassword(email);
  }

    @PostMapping("/resetPassword/{passwordResetToken}")
    public HashMap<String,String> resetPassword(@PathVariable String passwordResetToken, String newPassword){
        return authenticationServices.resetPassword(passwordResetToken, newPassword);
    }


}
