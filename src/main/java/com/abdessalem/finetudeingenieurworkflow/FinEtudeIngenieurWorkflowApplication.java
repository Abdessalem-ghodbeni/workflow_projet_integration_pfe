package com.abdessalem.finetudeingenieurworkflow;

import com.abdessalem.finetudeingenieurworkflow.Entites.Admin;
import com.abdessalem.finetudeingenieurworkflow.Entites.Role;
import com.abdessalem.finetudeingenieurworkflow.Entites.User;
import com.abdessalem.finetudeingenieurworkflow.Repository.IAdminRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableAspectJAutoProxy
@RequiredArgsConstructor
@SpringBootApplication
@EnableScheduling

@ComponentScan(basePackages = {"com.abdessalem.finetudeingenieurworkflow","com.abdessalem.finetudeingenieurworkflow.CorsCongiguration"})
public class FinEtudeIngenieurWorkflowApplication implements CommandLineRunner {
    private final IUserRepository userRepository;
    private final IAdminRepository adminRepository;
    public static void main(String[] args) {
        SpringApplication.run(FinEtudeIngenieurWorkflowApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        User adminAccount = userRepository.findByRole(Role.ADMIN);
        if (adminAccount == null) {
            Admin admin = new Admin();
            admin.setEmail("admin@esprit.tn");
            admin.setNom("admin");
            admin.setPrenom("admin");
            admin.setRole(Role.ADMIN);
            admin.setPassword(new BCryptPasswordEncoder().encode("admin"));
            adminRepository.save(admin);
        }
    }
}
