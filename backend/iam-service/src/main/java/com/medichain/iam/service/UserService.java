package com.medichain.iam.service;
 
import com.medichain.iam.client.NotificationFeignClient;

import com.medichain.iam.dto.CreateUserRequestDTO;

import com.medichain.iam.dto.LoginRequestDTO;

import com.medichain.iam.dto.LoginResponseDTO;

import com.medichain.iam.dto.NotificationRequestDTO;

import com.medichain.iam.dto.RegisterRequestDTO;

import com.medichain.iam.dto.UserResponseDTO;

import com.medichain.iam.entity.Role;

import com.medichain.iam.entity.User;

import com.medichain.iam.exception.InvalidCredentialsException;

import com.medichain.iam.exception.InvalidRoleException;

import com.medichain.iam.exception.UserAlreadyExistsException;

import com.medichain.iam.exception.UserNotFoundException;

import com.medichain.iam.exception.UserNotActiveException;

import com.medichain.iam.repository.UserRepository;

import com.medichain.iam.security.JwtUtil;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
 
import java.util.List;
 
@Service

@RequiredArgsConstructor

@Slf4j

public class UserService {
 
    private final UserRepository          userRepository;

    private final BCryptPasswordEncoder   encoder;

    private final JwtUtil                 jwtUtil;        // ← added

    private final NotificationFeignClient notificationClient;

    private final AuditLogService         auditLogService;
 
    // ── LOGIN (moved from AuthController) ──────────────────────────────────
 
    @Transactional(readOnly = true)

    public LoginResponseDTO login(LoginRequestDTO dto) {
 
        User user = userRepository.findByEmail(dto.getEmail())

                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
 
        if (!encoder.matches(dto.getPassword(), user.getPassword())) {

            auditLogService.log(user.getId(), "LOGIN_FAILED", "SESSION",

                    user.getEmail(), "Bad password");

            throw new InvalidCredentialsException("Invalid credentials");

        }
 
        if (!user.isActive()) {

            throw new UserNotActiveException("Account not yet activated by admin");

        }
 
        String token = jwtUtil.generateToken(

                user.getEmail(),

                user.getRole().name(),

                user.getId()

        );
 
        auditLogService.log(user.getId(), "LOGIN_SUCCESS", "SESSION",

                user.getEmail(), "Successful login");

        log.info("Login: userId={} role={}", user.getId(), user.getRole());
 
        return new LoginResponseDTO(

                user.getId(),

                token,

                user.getRole().name(),

                user.getName()

        );

    }
 
    // ── REGISTER ────────────────────────────────────────────────────────────
 
    @Transactional

    public void register(RegisterRequestDTO dto) {
 
        if (userRepository.existsByEmail(dto.getEmail())) {

            throw new UserAlreadyExistsException("Email already registered: " + dto.getEmail());

        }
 
        User user = User.builder()

                .name(dto.getName())

                .email(dto.getEmail())

                .phone(dto.getPhone())

                .password(encoder.encode(dto.getPassword()))

                .role(Role.UNASSIGNED)

                .active(false)

                .build();
 
        userRepository.save(user);

        log.info("New user registered: id={} email={}", user.getId(), user.getEmail());
 
        auditLogService.log(user.getId(), "USER_REGISTERED", "USER",

                String.valueOf(user.getId()), "Self-registration");
 
        sendNotificationSafely(

                user.getId(),

                "Registration successful. Your account is pending admin approval.",

                "IAM"

        );
 
        userRepository.findByEmail("admin@medichain.com").ifPresent(admin ->

                sendNotificationSafely(

                        admin.getId(),

                        "New user registered: " + user.getName() +

                        " (" + user.getEmail() + "). Please review and activate.",

                        "IAM"

                )

        );

    }
 
    // ── ADMIN USER MANAGEMENT ───────────────────────────────────────────────
 
    @Transactional

    public void createUserByAdmin(CreateUserRequestDTO dto) {
 
        if (userRepository.existsByEmail(dto.getEmail())) {

            throw new UserAlreadyExistsException("Email already registered: " + dto.getEmail());

        }
 
        Role role;

        try {

            role = Role.valueOf(dto.getRole().toUpperCase());

        } catch (IllegalArgumentException e) {

            throw new InvalidRoleException("Unknown role: " + dto.getRole());

        }
 
        User user = User.builder()

                .name(dto.getName())

                .email(dto.getEmail())

                .phone(dto.getPhone())

                .password(encoder.encode(dto.getPassword()))

                .role(role)

                .active(dto.isActive())

                .build();
 
        userRepository.save(user);

        log.info("Admin created user: id={} email={} role={}", user.getId(), user.getEmail(), role);
 
        auditLogService.log(user.getId(), "USER_CREATED_BY_ADMIN", "USER",

                String.valueOf(user.getId()), "Created with role: " + role);
 
        if (dto.isActive()) {

            sendNotificationSafely(

                    user.getId(),

                    "Your MediChain account is ready. Role: " + role,

                    "IAM"

            );

        }

    }
 
    @Transactional(readOnly = true)

    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll()

                .stream()

                .map(this::toResponseDTO)

                .toList();

    }
 
    @Transactional(readOnly = true)

    public UserResponseDTO getUserById(Long id) {

        return userRepository.findById(id)

                .map(this::toResponseDTO)

                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));

    }
 
    // ── HELPERS ─────────────────────────────────────────────────────────────
 
    private UserResponseDTO toResponseDTO(User user) {

        return new UserResponseDTO(

                user.getId(),

                user.getName(),

                user.getEmail(),

                user.getPhone(),

                user.getRole(),

                user.isActive()

        );

    }
 
    @CircuitBreaker(name = "notification-service", fallbackMethod = "notificationFallback")

    public void sendNotificationSafely(Long userId, String message, String category) {

        notificationClient.sendNotification(

                new NotificationRequestDTO(userId, null, message, category));

        log.debug("Notification sent to userId={}", userId);

    }
 
    public void notificationFallback(Long userId, String message,

                                      String category, Throwable t) {

        log.warn("Notification failed for userId={} reason={}", userId, t.getMessage());

    }

}
 