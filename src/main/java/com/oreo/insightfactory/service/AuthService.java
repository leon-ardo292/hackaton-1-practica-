package com.oreo.insightfactory.service;

import com.oreo.insightfactory.dto.LoginRequest;
import com.oreo.insightfactory.dto.LoginResponse;
import com.oreo.insightfactory.dto.RegisterRequest;
import com.oreo.insightfactory.dto.UserResponse;
import com.oreo.insightfactory.handlerexception.ConflictException;
import com.oreo.insightfactory.model.AppUser;
import com.oreo.insightfactory.model.JwtToken;
import com.oreo.insightfactory.model.UserRole;
import com.oreo.insightfactory.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        validateBranch(request.role(), request.branch());
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already exists");
        }

        AppUser user = new AppUser(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.role(),
                request.role() == UserRole.BRANCH ? request.branch().trim() : null
        );
        return UserResponse.from(userRepository.save(user));
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        AppUser user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        JwtToken token = jwtService.createToken(authentication, user);
        return new LoginResponse(token.value(), token.expiresInSeconds(), user.getRole(), user.getBranch());
    }

    private void validateBranch(UserRole role, String branch) {
        if (role == UserRole.BRANCH && (branch == null || branch.isBlank())) {
            throw new IllegalArgumentException("Branch is required for BRANCH users");
        }
        if (role == UserRole.CENTRAL && branch != null && !branch.isBlank()) {
            throw new IllegalArgumentException("Branch must be null for CENTRAL users");
        }
    }
}
