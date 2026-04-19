package com.ott.platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ContentResubmitDTO {
    @NotBlank private String title;
    private String genre;
    private int duration;
    private String ageRating;
    private String language;
    private String description;
}
