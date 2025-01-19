package com.abdessalem.finetudeingenieurworkflow.Entites;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "Users", uniqueConstraints = {
        @UniqueConstraint(name = "unique_email_identifiantEsprit", columnNames = {"email", "identifiantEsprit"})
})
public class User implements Serializable, UserDetails {
    @Id
    @GeneratedValue(strategy =GenerationType.AUTO)
    Long id;

    String nom;
    String prenom;
    String numeroTelephone;

    String email;

    String identifiantEsprit;
    String passwordResetToken;
    @Column(name = "password_reset_token_expiration")
    LocalDateTime passwordResetTokenExpiration;
    String password;
     String secret;
    @Enumerated(EnumType.STRING)
    Role role;


//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority(role.name()));
//    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
