package ifb.sbo.api.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return  http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/estudantes").permitAll()
                        .requestMatchers("/temas/professor/**").hasRole("PROFESSOR")
                        .requestMatchers("/temas/estudante/**").hasRole("ESTUDANTE")
                        .requestMatchers("/temas/**").authenticated()
                        .requestMatchers("/solicitacoes/estudante/**").hasRole("ESTUDANTE")
                        .requestMatchers("/professores/**").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.GET, "/areasInteresse").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.GET, "/cursos/**").authenticated()
                        .requestMatchers("/areasInteresse/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/cursos").hasRole("ADMINISTRADOR")
                        .requestMatchers("/**").hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
        public AuthenticationManager authenticationManager (AuthenticationConfiguration configuration) throws Exception {
            return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
