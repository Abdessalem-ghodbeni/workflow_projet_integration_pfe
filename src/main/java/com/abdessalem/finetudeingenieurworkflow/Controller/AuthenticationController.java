package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.IEtudiantRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ISocieteRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ITuteurRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IUserRepository;

import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.*;
import com.abdessalem.finetudeingenieurworkflow.utils.SendEmailServiceImp;
import jakarta.websocket.server.PathParam;
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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

  public static String uploadDirectory = System.getProperty("user.dir") + "/uploadUser";

  private final IAuthenticationServicesImp authenticationServices;
  private final SendEmailServiceImp sendEmailService;
  private final IUserRepository userRepository;


  private final TwoFactorAuthenticationService tfaService;
  private final PasswordEncoder passwordEncoder;
  private final PasswordResetService passwordResetService;
  private final AuthenticatorService authenticatorService;
  private final IJWTServicesImp jwtServices;
  private final ITuteurRepository tuteurRepository;
  private final IEtudiantRepository etudiantRepository;
  private final IHistoriqueServiceImp historiqueServiceImp;
  private final ISocieteRepository societeRepository;


  @PutMapping("/toogleStatus")
  public ResponseEntity<?> toogleStatusUser(@PathParam("id") Long id, @PathParam("userId") Long userId) {
    try {
      authenticationServices.toggleUserStatus(id,userId);

      return ResponseEntity.status(HttpStatus.OK).body("utilisateur status changed successfullllly");

    } catch (RessourceNotFound e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("Une erreur interne est survenue. Veuillez réessayer.");
    }
  }


@PostMapping("/registerInstructor")
public ResponseEntity<Tuteur> registerInstructor(@RequestParam("nom") String nom,
                                                 @RequestParam("prenom") String prenom,
                                                 @RequestParam("idUser") Long idUser,
                                                 @RequestParam("email") String email,
                                                 @RequestParam("password") String password,
                                                 @RequestParam("numeroTelephone") String numeroTelephone,
                                                 @RequestParam("identifiantEsprit") String identifiantEsprit,
                                                 @RequestParam("specialiteUp") String specialiteUp,
                                                 @RequestParam("nationality") String nationality,
                                                 @RequestParam(name = "is_Chef_Options",required = false) boolean is_Chef_Options,
                                                 @RequestParam(name = "is_Chef_departement",required = false) boolean is_Chef_departement,
                                                 @RequestParam("departementName") String departementName,
                                                 @RequestParam("dateEmbauche") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateEmbauche) throws IOException {
  Tuteur tuteur = new Tuteur();
  tuteur.setNom(nom);
  tuteur.setPrenom(prenom);
  tuteur.setEmail(email);
  tuteur.setPassword(password);
  tuteur.setIdentifiantEsprit(identifiantEsprit);
  tuteur.setNumeroTelephone(numeroTelephone);
  tuteur.setDateEmbauche(dateEmbauche);
  tuteur.setRole(Role.TUTEUR);
  tuteur.setSpecialiteUp(specialiteUp);
  tuteur.setNationality(nationality);
  tuteur.set_Chef_Options(is_Chef_Options);
  tuteur.set_Chef_departement((is_Chef_departement));
  tuteur.setDepartementName(departementName);

  // Génération de l'image des initiales
  String avatarFilename = generateInitialsAvatar(nom, prenom);
  tuteur.setImage(avatarFilename);

  // Sauvegarde du tuteur
  Tuteur savedTuteur = authenticationServices.RegisterInstructor(tuteur);
  if (savedTuteur != null) {
    String identifiantUnique = savedTuteur.getIdentifiantEsprit();
    String cin = savedTuteur.getNumeroTelephone();
    sendEmailService.sendInstructorEmail(email, nom + " " + prenom, identifiantUnique, cin);

    historiqueServiceImp.enregistrerAction(idUser, "CREATION",
            "Ajout d'un Tuteur avec ID " + savedTuteur.getId());

  }
  return ResponseEntity.ok(savedTuteur);
}

  /**
   * Génère une image avec les initiales et un fond de couleur aléatoire.
   */
  private String generateInitialsAvatar(String nom, String prenom) throws IOException {
    int width = 200;
    int height = 200;
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics = image.createGraphics();

    // Générer une couleur de fond aléatoire
//    Color backgroundColor = new Color((int) (Math.random() * 0x1000000));
//    graphics.setColor(backgroundColor);
    Color color1 = Color.RED; // Couleur rouge
    Color color2 = Color.BLACK; // Couleur noire
    GradientPaint gradient = new GradientPaint(0, 0, color1, width, height, color2, true);
    graphics.setPaint(gradient);
    graphics.fillRect(0, 0, width, height);

    // Ajouter les initiales
    String initials = prenom.charAt(0) + "" + nom.charAt(0);
    graphics.setColor(Color.WHITE);
    graphics.setFont(new Font("Arial", Font.BOLD, 100));
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int x = (width - fontMetrics.stringWidth(initials)) / 2;
    int y = ((height - fontMetrics.getHeight()) / 2) + fontMetrics.getAscent();
    graphics.drawString(initials.toUpperCase(), x, y);

    graphics.dispose();

    // Sauvegarder l'image
    String filename = UUID.randomUUID().toString() + "_avatar.png";
    Path filePath = Paths.get(uploadDirectory, filename);
    if (!Files.exists(filePath.getParent())) {
      Files.createDirectories(filePath.getParent());
    }
    ImageIO.write(image, "png", filePath.toFile());
    return filename;
  }

  @PostMapping("/registerSociete")
  public ResponseEntity<Societe> registerSociete(@RequestParam("nom") String nom,
                                                   @RequestParam(name="email") String email,
                                                 @RequestParam("idUser") Long idUser,

                                                   @RequestParam("numeroTelephone") String numeroTelephone,
                                                 @RequestParam(name ="codePostal",required = false) String codePostal,
                                                   @RequestParam(name="adresse",required = false) String adresse,
                                                   @RequestParam("ville") String ville,
                                                 @RequestParam(name="siteWeb",required = false) String siteWeb,
                                                 @RequestParam(name="pays",required = false) String pays,
                                                 @RequestParam(name="logo",required = false) MultipartFile file,
                                                 @RequestParam(name="secteurActivite",required = false) String secteurActivite) throws IOException {
    String generatedPassword = generateRandomPassword();
    Societe societe = new Societe();
    societe.setNom(nom);
    societe.setEmail(email);
    societe.setPassword(generatedPassword);
    societe.setNumeroTelephone(numeroTelephone);
    societe.setRole(Role.SOCIETE);
societe.setSecteurActivite(secteurActivite);
societe.setCodePostal(codePostal);
societe.setAdresse(adresse);
societe.setVille(ville);
societe.setSiteWeb(siteWeb);
societe.setPays(pays);

if (file != null && !file.isEmpty()){
  String originalFilename = file.getOriginalFilename();
  String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
  Path fileNameAndPath = Paths.get(uploadDirectory, uniqueFilename);
  if (!Files.exists(fileNameAndPath.getParent())) {
    Files.createDirectories(fileNameAndPath.getParent());
  }
  Files.write(fileNameAndPath, file.getBytes());
  societe.setLogo(uniqueFilename);
}

else {
  // Génération de l'image des initiales
  String avatarFilename = generateInitialsAvatarSociete(nom);
  societe.setLogo(avatarFilename);
}


    // Sauvegarde du tuteur
    Societe savedSociete = authenticationServices.RegisterSociete(societe);
    if (savedSociete != null) {
//      String passwordd = savedSociete.getPassword();
      String emaill = savedSociete.getEmail();
      String nomm=savedSociete.getNom();
      sendEmailService.sendSocieteEmail(emaill, nomm, generatedPassword);
      historiqueServiceImp.enregistrerAction(idUser, "CREATION",
              "Ajout d'un compte entrprise partenaire avec ID " + savedSociete.getId());


    }
    return ResponseEntity.ok(savedSociete);
  }
  private String generateRandomPassword() {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
    StringBuilder password = new StringBuilder();
    Random random = new Random();
    int length = 12;

    for (int i = 0; i < length; i++) {
      password.append(chars.charAt(random.nextInt(chars.length())));
    }

    return password.toString();
  }
  private String generateInitialsAvatarSociete(String nom) throws IOException {
    int width = 200;
    int height = 200;
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics = image.createGraphics();

    // Générer une couleur de fond aléatoire
    Color backgroundColor = new Color((int) (Math.random() * 0x1000000));
    graphics.setColor(backgroundColor);
    graphics.fillRect(0, 0, width, height);

    // Extraire les initiales
    String[] parts = nom.split(" ");
    String initials;
    if (parts.length > 1) {
      // Si le nom contient un espace, prendre la première lettre du prénom et la première lettre du nom
      initials = parts[0].charAt(0) + "" + parts[1].charAt(0);
    } else {
      // Sinon, prendre les deux premières lettres du nom
      initials = nom.substring(0, Math.min(2, nom.length()));
    }

    // Ajouter les initiales à l'image
    graphics.setColor(Color.WHITE);
    graphics.setFont(new Font("Arial", Font.BOLD, 100));
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int x = (width - fontMetrics.stringWidth(initials)) / 2;
    int y = ((height - fontMetrics.getHeight()) / 2) + fontMetrics.getAscent();
    graphics.drawString(initials.toUpperCase(), x, y);

    graphics.dispose();

    // Sauvegarder l'image
    String filename = UUID.randomUUID().toString() + "_avatar.png";
    Path filePath = Paths.get(uploadDirectory, filename);
    if (!Files.exists(filePath.getParent())) {
      Files.createDirectories(filePath.getParent());
    }
    ImageIO.write(image, "png", filePath.toFile());
    return filename;
  }
  // ajout etudiant
  @PostMapping("/registerEtudiant")
  public ResponseEntity<Etudiant> registerEtudiant(@RequestParam("nom") String nom,
                                                   @RequestParam("prenom") String prenom,
                                                   @RequestParam("email") String email,
                                                   @RequestParam("password") String password,
                                                   @RequestParam("numeroTelephone") String numeroTelephone,
                                                   @RequestParam("identifiantEsprit") String identifiantEsprit,
                                                   @RequestParam("specialite") String specialite,
                                                   @RequestParam("nationality") String nationality,
                                                   @RequestParam("classe") String classe, @RequestParam("idUser") Long idUser,
                                                 @RequestParam("niveau") Long niveau,
                                                   @RequestParam("dateNaissance") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateNaissance) throws IOException {
    Etudiant etudiant = new Etudiant();
    etudiant.setNom(nom);
    etudiant.setPrenom(prenom);
    etudiant.setEmail(email);
    etudiant.setPassword(password);
    etudiant.setIdentifiantEsprit(identifiantEsprit);
    etudiant.setNumeroTelephone(numeroTelephone);
    etudiant.setDateNaissance(dateNaissance);
    etudiant.setRole(Role.ETUDIANT);
    etudiant.setSpecialite(specialite);
    etudiant.setNationality(nationality);
    etudiant.setClasse(classe);
    etudiant.setNiveau(niveau);


    String avatarFilename = generateInitialsAvatar(nom, prenom);
    etudiant.setImage(avatarFilename);


    Etudiant savedEtudiant = authenticationServices.registerEtudiant(etudiant);
    if (savedEtudiant != null) {
      String identifiantUnique = savedEtudiant.getIdentifiantEsprit();
      String cin = savedEtudiant.getNumeroTelephone();
      sendEmailService.sendInstructorEmail(email, nom + " " + prenom, identifiantUnique, cin);
      historiqueServiceImp.enregistrerAction(idUser, "CREATION",
              "Ajout d'un etudiant avec ID " + savedEtudiant.getId());


    }
    return ResponseEntity.ok(savedEtudiant);
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
  public ResponseEntity<?> loginn(@RequestBody User user) {
   try{
     Optional<User> user_=userRepository.findByEmail(user.getEmail());
     if (user_.isPresent() && !user_.get().isActive()){
       return new ResponseEntity<>("ACCOUNT EST NON ACTIVE",HttpStatus.OK);
     }else {


     AuthenticationResponse response=authenticationServices.login(user.getEmail(), user.getPassword());
     return new ResponseEntity<>(response,HttpStatus.OK);
     }
   }catch (Exception exception){
     return new ResponseEntity<>(exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
   }
  }

  @PostMapping("/refreshToken")
  public AuthenticationResponse refreshToken(@RequestBody RefreshTokenRequest refreshToken) {
      return authenticationServices.refreshToken(refreshToken);
  }


@PostMapping("/forgot-password")
public ResponseEntity<String> forgotPassword(@RequestParam String email) {
  Optional<User> userOptional = userRepository.findByEmail(email);
  if (userOptional.isPresent()) {
    String token = UUID.randomUUID().toString();
    User user = userOptional.get();
    passwordResetService.createPasswordResetTokenForUser(user, token);
    String resetLink = "http://localhost:4200/confirmer-reset-password?token=" + token+ "&email=" + email;
    passwordResetService.sendPasswordResetEmail(email, resetLink);
    return ResponseEntity.ok("Email de réinitialisation envoyé!");
  } else {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé"); }
}



  @PostMapping("/verify-otp")
  public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody VerificationRequest verificationRequest) {
    String email = verificationRequest.getEmail();
    String code = verificationRequest.getCode();
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
    boolean isCodeValid = authenticatorService.verifyCode(user.getSecret(), Integer.parseInt(code));
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

  @PutMapping("/updateTuteur/{id}")
  public ResponseEntity<Tuteur> updateTuteur(@PathVariable Long id,
                                             @RequestParam(required = false) String nom,
                                             @RequestParam(required = false) String prenom,
                                             @RequestParam(required = false) String email,
                                             @RequestParam(required = false) String password,
                                             @RequestParam(required = false) String numeroTelephone,
                                             @RequestParam(required = false) String specialiteUp,
                                             @RequestParam(required = false) String nationality,
                                             @RequestParam(required = false) Boolean is_Chef_Options,
                                             @RequestParam(required = false) String githubToken,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateEmbauche,
                                             @RequestParam(required = false) MultipartFile image) {
    Optional<Tuteur> optionalTuteur = tuteurRepository.findById(id);

    if (!optionalTuteur.isPresent()) {
      return ResponseEntity.notFound().build();
    }

    Tuteur tuteur = optionalTuteur.get();

    // Mettre à jour les informations si elles sont fournies
    if (nom != null) tuteur.setNom(nom);
    if (prenom != null) tuteur.setPrenom(prenom);
    if (email != null) tuteur.setEmail(email);
    if (password != null) tuteur.setPassword(password);
    if (numeroTelephone != null) tuteur.setNumeroTelephone(numeroTelephone);
    if (specialiteUp != null) tuteur.setSpecialiteUp(specialiteUp);
    if (nationality != null) tuteur.setNationality(nationality);
    if (is_Chef_Options != null) tuteur.set_Chef_Options(is_Chef_Options);
    if (dateEmbauche != null) tuteur.setDateEmbauche(dateEmbauche);
    if (githubToken != null && !githubToken.isBlank()) {
      tuteur.setGithubToken(githubToken);
    }
    // Gestion de l'image
    if (image != null && !image.isEmpty()) {
      String imageName = saveImage(image);
      tuteur.setImage(imageName);
    }

    Tuteur updatedTuteur = tuteurRepository.save(tuteur);
    // maraja3ch tocken te3 github 5atrou secure
    updatedTuteur.setGithubToken(null);
    return ResponseEntity.ok(updatedTuteur);
  }

  private String saveImage(MultipartFile image) {
    // Implémentez votre logique de sauvegarde ici
    // Par exemple, en sauvegardant l'image dans un répertoire spécifique
    try {
      String filename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
      Path filePath = Paths.get(uploadDirectory, filename);
      Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
      return filename; // Retourner le nom du fichier sauvegardé
    } catch (IOException e) {
      throw new RuntimeException("Erreur lors de la sauvegarde de l'image", e);
    }
  }




  @PutMapping("/updateEtudiant/{id}")
  public ResponseEntity<Etudiant> updateEtudiant(@PathVariable Long id,
                                             @RequestParam(required = false) String nom,
                                             @RequestParam(required = false) String prenom,
                                             @RequestParam(required = false) String email,
                                             @RequestParam(required = false) String password,
                                             @RequestParam(required = false) String numeroTelephone,
                                             @RequestParam(required = false) String specialite,
                                             @RequestParam(required = false) String nationality,
                                             @RequestParam(required = false) String classe,
                                               @RequestParam(required = false) Long niveau,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateNaissance,
                                             @RequestParam(required = false) MultipartFile image) {
    Optional<Etudiant> optionalEtudiant = etudiantRepository.findById(id);

    if (!optionalEtudiant.isPresent()) {
      return ResponseEntity.notFound().build();
    }

    Etudiant etudiant = optionalEtudiant.get();

    // Mettre à jour les informations si elles sont fournies
    if (nom != null) etudiant.setNom(nom);
    if (prenom != null) etudiant.setPrenom(prenom);
    if (email != null) etudiant.setEmail(email);
    if (password != null) etudiant.setPassword(password);
    if (numeroTelephone != null) etudiant.setNumeroTelephone(numeroTelephone);
    if (specialite != null) etudiant.setSpecialite(specialite);
    if (nationality != null) etudiant.setNationality(nationality);
    if (classe != null) etudiant.setClasse(classe);
    if (niveau != null) etudiant.setNiveau(niveau);
    if (dateNaissance != null) etudiant.setDateNaissance(dateNaissance);

    // Gestion de l'image
    if (image != null && !image.isEmpty()) {
      String imageName = saveImage(image); // Méthode pour sauvegarder l'image
      etudiant.setImage(imageName);
    }

    // Sauvegarder les modifications
    Etudiant updatedEtudiant = etudiantRepository.save(etudiant);
    historiqueServiceImp.enregistrerAction(id, "Modification",
            "Modification informations personnel " + id);

    return ResponseEntity.ok(updatedEtudiant);
  }



  @PutMapping("/update/Societerofile/{id}")
  public ResponseEntity<Societe> updateSocieterofile(@PathVariable Long id,
                                             @RequestParam(required = false) String nom,
                                             @RequestParam(required = false) String email,
                                             @RequestParam(required = false) String numeroTelephone,
                                            @RequestParam(required = false) String siteWeb,
                                            @RequestParam(required = false) String secteurActivite,

                                             @RequestParam(required = false) String pays,

                                                     @RequestParam(required = false) String adresse,
                                                     @RequestParam(required = false) String ville,
                                                     @RequestParam(required = false) String codePostal,
                                             @RequestParam(required = false) MultipartFile logo) {
    Optional<Societe> optionalSociete = societeRepository.findById(id);

    if (!optionalSociete.isPresent()) {
      return ResponseEntity.notFound().build();
    }

    Societe societe = optionalSociete.get();


    if (nom != null) societe.setNom(nom);
    if (siteWeb != null) societe.setPrenom(siteWeb);
    if (email != null) societe.setEmail(email);

    if (numeroTelephone != null) societe.setNumeroTelephone(numeroTelephone);
    if (secteurActivite != null) societe.setSecteurActivite(secteurActivite);
    if (pays != null) societe.setPays(pays);
    if (adresse != null) societe.setAdresse(adresse);
    if (ville != null) societe.setVille(ville);
    if (codePostal != null) societe.setCodePostal(codePostal);

    // Gestion de l'image
    if (logo != null && !logo.isEmpty()) {
      String imageName = saveImage(logo);
      societe.setLogo(imageName);
    }


    Societe updatedSociete = societeRepository.save(societe);
    historiqueServiceImp.enregistrerAction(id, "Modification",
            "Modification informations personnel " + id);
    return ResponseEntity.ok(updatedSociete);
  }





 @PutMapping(path = "change-password")
 public ResponseEntity<ApiResponse> changePassword(@RequestBody ChangePasswordRequest request) {
   try {
     ApiResponse response = authenticationServices.changePassword(request);
     if (!response.isSuccess()) {
       return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
     } else {
       historiqueServiceImp.enregistrerAction(request.getUserId(), "Modification", "Changement du mot de passe");
       return new ResponseEntity<>(response, HttpStatus.OK);
     }
   } catch (Exception exception) {
     return new ResponseEntity<>(new ApiResponse(exception.getCause().getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
   }
  }



}
