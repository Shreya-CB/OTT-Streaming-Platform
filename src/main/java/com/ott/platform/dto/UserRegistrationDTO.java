package com.ott.platform.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserRegistrationDTO {
    @NotBlank private String name;
    @Email @NotBlank private String email;
    @NotBlank @Size(min = 6) private String password;
    /** VIEWER | CONTENT_CREATOR | MODERATOR | ADMINISTRATOR */
    @NotBlank private String userType;
}
