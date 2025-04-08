package com.abdessalem.finetudeingenieurworkflow.JWTConfiguration;

import com.abdessalem.finetudeingenieurworkflow.Entites.Role;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IUserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {


    private final JwtAuthenticationFilter jwtAuthFilter;
    private final IUserServices userServices;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers("/**")
                                .permitAll()
                                .requestMatchers("/**").hasAnyRole(Role.ADMIN.name(), Role.ETUDIANT.name(),Role.TUTEUR.name(),Role.SOCIETE.name())
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .logout(logout ->
//                        logout.logoutUrl("/auth/logout")
//                                .addLogoutHandler(logoutHandler())
//                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
//                )
        ;

        return http.build();
    }


//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(req ->
//                        req
//                                .requestMatchers(
//                                        "/auth/login",
//                                        "/swagger-ui/**",
//                                        "/v3/api-docs/**","/auth/{filename}"
//                                ).permitAll()
//                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Autoriser OPTIONS
//                                .requestMatchers(
//                                        "/sujet/**",
//                                        "/etudiant/**",
//                                        "/sujet/**",
//                                        "/historique/**",
//                                        "/tuteur/{id}","/tuteur/**"
//                                ).hasAnyAuthority("TUTEUR", "ADMIN")
//                                .requestMatchers("/sujet/all", "/auth",
//                                        "/historique/{id_user}","/auth/updateTuteur", "/tuteur/{id}","/tuteur/list/{tuteurId}",
//                                        "/sujet/**",
//                                        "/form/**",
//                                        "/equipe/**",
//                                        "/historique/**",
//
//                                        "/equipe/construire/**",
//                                        "/equipe/all",
//                                        "/equipe/grouped/**",
//                                        "/equipe/affecter/**",
//                                        "/equipe/retirer/**",
//                                        "/equipe/par-specialite-annee",
//                                        "/equipe/par-etudiant/**",
//                                        "/form/ajouter-formulaire",
//                                        "/form/tous/**",
//                                        "/form/update/**",
//                                        "/form/delete/**",
//                                        "/form/accessibility/**",
//                                        "/sujet/ajouter",
//                                        "/sujet/update/**",
//                                        "/sujet/delete/**",
//                                        "/sujet/change/etat",
//                                        "/sujet/rendreSujetsVisibles",
//                                        "/historique/{id_user}",
//                                        "/historique/totale/liste").hasAuthority("TUTEUR")
//                                .requestMatchers(
//                                        "/auth/registerInstructor",
//                                        "/auth/registerSociete",
//                                        "/auth/registerEtudiant",
//                                        "/tuteur/**",
//                                        "/societe/**","tuteur/totale/liste"
//
//                                ).hasAuthority("ADMIN")
//
//                                .anyRequest().authenticated()
//                )
//                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
//                .authenticationProvider(authenticationProvider())
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userServices.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


}
