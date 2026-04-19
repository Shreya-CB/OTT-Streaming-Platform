package com.ott.platform.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RatingDTO {
    @NotNull private Long viewerId;
    @NotBlank private String contentTitle;
    @Min(1) @Max(5) private int rating;
    private String comment;
}
