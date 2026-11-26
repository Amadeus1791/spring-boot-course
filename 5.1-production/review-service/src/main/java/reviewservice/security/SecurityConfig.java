package reviewservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain api(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Reading is open: anyone may see reviews and the rating.
                        .requestMatchers(HttpMethod.GET, "/api/reviews/*", "/api/events/*/reviews",
                                "/api/events/*/rating").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/events/*/reviews").hasRole("CUSTOMER")
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // PUT /api/reviews/{id}: a login here, whose review it is the service decides.
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    // Users in memory: only for the course. Chapter 4.3 replaces them with tokens.
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername("alice@example.com")
                        .password(encoder.encode("alice")).roles("CUSTOMER").build(),
                User.withUsername("ben@example.com")
                        .password(encoder.encode("ben")).roles("CUSTOMER").build(),
                User.withUsername("olivia@example.com")
                        .password(encoder.encode("olivia")).roles("ORGANIZER").build());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
