package com.ott.platform.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RatingResponseDTO {
    private String contentTitle;
    private String viewerName;
    private int rating;
    private String comment;
}
