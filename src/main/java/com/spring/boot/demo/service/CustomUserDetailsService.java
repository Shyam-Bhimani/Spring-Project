package com.spring.boot.demo.service;

import com.spring.boot.demo.entity.AppUser;
import com.spring.boot.demo.repository.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public CustomUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // .roles(...) automatically adds the "ROLE_" prefix required by .hasRole(...)
        String roleWithoutPrefix = appUser.getRole().replace("ROLE_", "");

        return User.builder()
                .username(appUser.getEmail())
                .password(appUser.getPassword())
                .roles(roleWithoutPrefix)
                .disabled(!appUser.isEnabled())
                .build();
    }
}
