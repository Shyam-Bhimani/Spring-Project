package com.spring.boot.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

//    @Bean
//    public UserDetailsService userDetailsService(){
//        UserDetails customer = User.withUsername("shyam")
//                .password("{noop}secret123")
//                .roles("CUSTOMER")
//                .build();
//
//        UserDetails admin = User.withUsername("admin")
//                .password("{noop}admin123")
//                .roles("ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(customer,admin);
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Public endpoints
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

                        // 2. Loan creation -> CUSTOMER only
                        .requestMatchers(HttpMethod.POST, "/api/loans/apply").hasRole("CUSTOMER")

                        // 3. Approve / Reject -> ADMIN only
                        .requestMatchers(HttpMethod.POST, "/api/loans/*/approve", "/api/loans/*/reject").hasRole("ADMIN")

                        // 4. View loans -> Both CUSTOMER (own loans) and ADMIN (all loans)
                        .requestMatchers(HttpMethod.GET, "/api/loans/**").hasAnyRole("CUSTOMER", "ADMIN")

                        // 5. Admin-only APIs
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 4. Keep anyRequest().authenticated() as the final rule
                        .anyRequest().authenticated()
                ).httpBasic(Customizer.withDefaults());
               // .formLogin(Customizer.withDefaults());
        return http.build();
    }
}
