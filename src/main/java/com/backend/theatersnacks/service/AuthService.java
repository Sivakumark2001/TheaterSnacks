package com.backend.theatersnacks.service;

import com.backend.theatersnacks.dto.request.SendOtpRequest;
import com.backend.theatersnacks.dto.request.VerifyOtpRequest;
import com.backend.theatersnacks.dto.response.AuthResponse;
import com.backend.theatersnacks.entity.Role;
import com.backend.theatersnacks.entity.User;
import com.backend.theatersnacks.repository.RoleRepository;
import com.backend.theatersnacks.repository.UserRepository;
import com.backend.theatersnacks.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;

    public void sendOtp(SendOtpRequest request) {
        String otp = otpService.generateOtp(request.getPhoneNumber());
        otpService.sendOtpAsSms(request.getPhoneNumber(), otp);
    }

    public void sendOtpEmail(SendOtpRequest request) {
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            String otp = otpService.generateOtp(request.getPhoneNumber());
            otpService.sendOtpToWhatsapp(request.getPhoneNumber(), otp);
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            String otp = otpService.generateOtp(request.getEmail());
            otpService.sendOtpToEmail(request.getEmail(), otp);
        }
    }

    @Transactional
    public AuthResponse verifyOtpAndLogin(VerifyOtpRequest request) {
        boolean valid = otpService.verifyOtp(request.getPhoneNumber(), request.getOtp());
        if (!valid)
            throw new RuntimeException("Invalid or expired OTP");

        Optional<User> userOpt = userRepository.findByPhoneNumber(request.getPhoneNumber());
        boolean isNewUser = false;

        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            user = User.builder()
                    .phoneNumber(request.getPhoneNumber())
                    .isPhoneVerified(true)
                    .isActive(true)
                    .build();
            Role customerRole = roleRepository.findByName("CUSTOMER")
                    .orElseGet(() -> roleRepository.save(new Role(null, "CUSTOMER", "Customer Role", null)));
            user.setRoles(Set.of(customerRole));
            user = userRepository.save(user);
            isNewUser = true;
        }

        String mainRole = user.getRoles().iterator().next().getName();
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getPhoneNumber(), mainRole);
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getPhoneNumber(), mainRole);

        // (Optionally: Save refresh token in DB via UserSession here)

        return new AuthResponse(accessToken, refreshToken, isNewUser);
    }
}
