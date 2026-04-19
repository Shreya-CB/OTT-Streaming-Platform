package com.ott.platform.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SubscriptionRequestDTO {
    @NotNull  private Long   viewerId;
    @NotNull  private Long   planId;
    @NotBlank private String paymentMethod;  // ONLINE | etc.
}
