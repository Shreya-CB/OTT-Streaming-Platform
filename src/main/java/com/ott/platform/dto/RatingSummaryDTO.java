package com.ott.platform.dto;

import com.ott.platform.model.RatingReview;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RatingSummaryDTO {
    private String contentTitle;
    private String viewerName;
    private int rating;
    private String comment;

    public static RatingSummaryDTO fromRating(RatingReview rating) {
        return new RatingSummaryDTO(
                rating.getContent() != null ? rating.getContent().getTitle() : null,
                rating.getViewer() != null ? rating.getViewer().getName() : null,
                rating.getRating(),
                rating.getComment());
    }
}
