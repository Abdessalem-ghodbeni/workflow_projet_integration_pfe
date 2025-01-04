package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Role;
import com.abdessalem.finetudeingenieurworkflow.Entites.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    User findByRole(Role role);
    Optional<User> findByPasswordResetToken(String passwordResetToken);

    Optional<User> findByIdentifiantEsprit(String identifiantEsprit);


}
