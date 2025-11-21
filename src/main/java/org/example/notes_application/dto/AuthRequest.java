package org.example.notes_application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @Email(message = "Email should be valid")
    @NotBlank
    private String email;

    private String password;
}
