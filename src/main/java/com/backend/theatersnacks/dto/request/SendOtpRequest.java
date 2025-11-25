package com.backend.theatersnacks.dto.request;

import lombok.Data;

@Data
public class SendOtpRequest {
    private String phoneNumber;
    private String email;
}
