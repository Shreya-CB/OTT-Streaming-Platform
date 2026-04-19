package com.ott.platform.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ReviewSummaryDTO {
    private String contentTitle;
    private String moderatorName;
    private String decision;
    private String comments;
    private LocalDateTime reviewedAt;
}
