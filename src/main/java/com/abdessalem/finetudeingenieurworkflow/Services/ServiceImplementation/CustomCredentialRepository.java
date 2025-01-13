package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.User;
import com.abdessalem.finetudeingenieurworkflow.Repository.IUserRepository;
import com.warrenstrange.googleauth.ICredentialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomCredentialRepository implements ICredentialRepository {
    private final IUserRepository userRepository;

    @Override
    public String getSecretKey(String userName) {
        log.warn("Clé secrète non trouvée pour l'utilisateur : {}", userName);

        com.abdessalem.finetudeingenieurworkflow.Entites.User user = userRepository.findByEmail(userName).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        log.info("holla",user.getSecret());
        return user.getSecret();
    }

    @Override
    public void saveUserCredentials(String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {
        log.info("Enregistrement de la clé secrète pour l'utilisateur : {}", userName);
        log.info("Clé secrète : {}", secretKey);

        User user = userRepository.findByEmail(userName).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setSecret(secretKey);
        userRepository.save(user);
    }

}