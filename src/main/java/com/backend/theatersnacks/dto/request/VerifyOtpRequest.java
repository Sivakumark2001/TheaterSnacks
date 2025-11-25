package com.backend.theatersnacks.dto.request;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String phoneNumber;
    private String email;
    private String otp;
}
