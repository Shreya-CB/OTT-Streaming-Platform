package com.ott.platform.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ReviewDecisionDTO {
    private Long   contentId;
    private String contentTitle;
    @NotNull private Long   moderatorId;
    @NotBlank private String decision;   // APPROVED | REJECTED | FLAGGED
    private String comments;
}
