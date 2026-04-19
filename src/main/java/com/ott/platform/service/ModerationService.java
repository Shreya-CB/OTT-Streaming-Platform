package com.ott.platform.service;

import com.ott.platform.dto.ReviewDecisionDTO;
import com.ott.platform.model.Review;
import java.util.List;

public interface ModerationService {
    Review reviewContent(ReviewDecisionDTO dto);
    List<Review> getReviewsByContent(Long contentId);
    List<Review> getReviewsByModerator(Long moderatorId);
    List<Review> getReviewsForCreator(Long creatorId);
}
