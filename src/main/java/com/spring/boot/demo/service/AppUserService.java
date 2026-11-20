package com.spring.boot.demo.service;

import com.spring.boot.demo.dto.RegisterRequest;
import com.spring.boot.demo.dto.UserResponse;
import com.spring.boot.demo.entity.AppUser;
import com.spring.boot.demo.exception.DuplicateCustomerEmailException;
import com.spring.boot.demo.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserService(AppUserRepository appUserRepository,PasswordEncoder passwordEncoder){
        this.appUserRepository=appUserRepository;
        this.passwordEncoder=passwordEncoder;
    }

    @Transactional
    public UserResponse registerCustomer(RegisterRequest request){
        String normalizesEmail = request.email().trim().toLowerCase();
        if(appUserRepository.existsByEmail(normalizesEmail)){
            throw new DuplicateCustomerEmailException("Email already Exist!!");
        }
        String encodedPassword = passwordEncoder.encode(request.password());

        AppUser newUser = new AppUser(normalizesEmail,encodedPassword,"CUSTOMER",true);

        AppUser savedUser = appUserRepository.save(newUser);
        return UserResponse.fromEntity(savedUser);
    }
}
