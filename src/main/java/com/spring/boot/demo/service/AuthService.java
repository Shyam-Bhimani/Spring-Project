package com.spring.boot.demo.service;

import com.spring.boot.demo.dto.AuthenticationResponse;
import com.spring.boot.demo.dto.LoginRequest;
import com.spring.boot.demo.dto.RefreshTokenRequest;
import com.spring.boot.demo.dto.RefreshTokenResponse;
import com.spring.boot.demo.entity.AppUser;
import com.spring.boot.demo.entity.RefreshToken;
import com.spring.boot.demo.repository.AppUserRepository;
import com.spring.boot.demo.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AppUserRepository appUserRepository;

    public AuthService(
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            AppUserRepository appUserRepository) {

        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.appUserRepository = appUserRepository;
    }

    public AuthenticationResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        request.username()
                );

        AppUser user = appUserRepository
                .findByEmail(request.username().trim().toLowerCase())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        String accessToken =
                jwtService.generateToken(authentication);

        String refreshToken =
                refreshTokenService.createRefreshToken(user);

        return new AuthenticationResponse(
                accessToken,
                refreshToken,
                "Bearer"
        );
    }

    public void logout(RefreshTokenRequest request) {

        refreshTokenService.revokeRefreshToken(
                request.refreshToken()
        );
    }

    @Transactional
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {

        RefreshToken existingToken =
                refreshTokenService.verifyRefreshToken(
                        request.refreshToken()
                );

        AppUser user = existingToken.getUser();

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        user.getEmail()
                );

        if (!userDetails.isEnabled()
                || !userDetails.isAccountNonLocked()
                || !userDetails.isAccountNonExpired()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User account is not active"
            );
        }

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        String newAccessToken =
                jwtService.generateToken(authentication);

        String newRefreshToken =
                refreshTokenService.rotateRefreshToken(
                        request.refreshToken()
                );

        return new RefreshTokenResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer"
        );
    }
}
