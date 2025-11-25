package com.backend.theatersnacks.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender mailSender;

    @Value("${otp.expiry-minutes:5}")
    private int expiryMinutes;

    @Value("${twilio.account-sid}")
    private String twilioAccountSid;

    @Value("${twilio.auth-token}")
    private String twilioAuthToken;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    @PostConstruct
    public void initTwilio() {
        if (twilioAccountSid != null && !twilioAccountSid.isEmpty() &&
                twilioAuthToken != null && !twilioAuthToken.isEmpty()) {
            Twilio.init(twilioAccountSid, twilioAuthToken);
        }
    }

    private final Random random = new SecureRandom();

    private String otpKey(String phoneNumber) {
        return "otp:" + phoneNumber;
    }

    public String generateOtp(String phoneNumber) {
        String otp = String.format("%06d", random.nextInt(1_000_000));
        redisTemplate.opsForValue().set(otpKey(phoneNumber), otp, expiryMinutes, TimeUnit.MINUTES);
        return otp;
    }

    public boolean verifyOtp(String phoneNumber, String otp) {
        String key = otpKey(phoneNumber);
        String storedOtp = redisTemplate.opsForValue().get(key);

        // Optionally, delete OTP after verification for security
        if (storedOtp != null && storedOtp.equals(otp)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    // For development: simulate sending SMS (production: integrate Twilio, etc.)
    public void sendOtpAsSms(String phoneNumber, String otp) {
        System.out.println("[DEV] Sending OTP " + otp + " to " + phoneNumber);
        // If integrating SMS: call provider API here (e.g., Twilio)
    }

    public void sendOtpToWhatsapp(String phoneNumber, String otp) {
        try {
            if (twilioAccountSid == null || twilioAccountSid.isEmpty()) {
                System.out
                        .println("[DEV] Twilio not configured. Simulating WhatsApp OTP " + otp + " to " + phoneNumber);
                return;
            }

            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + phoneNumber),
                    new PhoneNumber("whatsapp:" + twilioPhoneNumber),
                    "Your TheaterSnacks OTP is: " + otp)
                    .create();
            System.out.println("WhatsApp OTP sent to " + phoneNumber + ", SID: " + message.getSid());
        } catch (Exception e) {
            System.err.println("Failed to send WhatsApp OTP: " + e.getMessage());
        }
    }

    public void sendOtpToEmail(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Your OTP Code");
            message.setText("Your OTP code is: " + otp);
            mailSender.send(message);
            System.out.println("OTP sent to email: " + email);
        } catch (Exception e) {
            System.err.println("Failed to send OTP to email: " + e.getMessage());
            // Handle exception appropriately
        }
    }
}
