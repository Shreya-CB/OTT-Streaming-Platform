package com.ott.platform.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ContentUploadDTO {
    @NotBlank private String title;
    @NotNull  private Long   creatorId;
    private String genre;
    private int    duration;
    private String ageRating;
    private String language;
    private String description;
}
