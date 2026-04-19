package com.ott.platform.service.impl;

import com.ott.platform.dto.ReviewDecisionDTO;
import com.ott.platform.model.*;
import com.ott.platform.repository.*;
import com.ott.platform.service.ModerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Implements the moderation flow from the Activity Diagram (Content Upload & Moderation).
 *
 * State transitions driven here (mirrors State Diagram – Content Moderation):
 *   SUBMITTED   → UNDER_REVIEW  (when review starts)
 *   UNDER_REVIEW → APPROVED     (decision = APPROVED)
 *   UNDER_REVIEW → REJECTED     (decision = REJECTED)
 *   UNDER_REVIEW → FLAGGED      (decision = FLAGGED for policy violation)
 *
 * GRASP – Controller: ModerationService acts as controller for the review use-case.
 */
@Service
public class ModerationServiceImpl implements ModerationService {

    private final ContentRepository      contentRepo;
    private final ModeratorRepository    moderatorRepo;
    private final ReviewRepository       reviewRepo;
    private final NotificationRepository notificationRepo;
    private final ContentCreatorRepository creatorRepo;

    @Autowired
    public ModerationServiceImpl(ContentRepository contentRepo,
                                  ModeratorRepository moderatorRepo,
                                  ReviewRepository reviewRepo,
                                  NotificationRepository notificationRepo,
                                  ContentCreatorRepository creatorRepo) {
        this.contentRepo      = contentRepo;
        this.moderatorRepo    = moderatorRepo;
        this.reviewRepo       = reviewRepo;
        this.notificationRepo = notificationRepo;
        this.creatorRepo      = creatorRepo;
    }

    @Override
    public Review reviewContent(ReviewDecisionDTO dto) {

        Content content = findContent(dto);

        Moderator moderator = moderatorRepo.findById(dto.getModeratorId())
                .orElseThrow(() -> new RuntimeException("Moderator not found: " + dto.getModeratorId()));

        // Transition: SUBMITTED → UNDER_REVIEW (if not already)
        if (content.getStatus() == ContentStatus.SUBMITTED) {
            content.setStatus(ContentStatus.UNDER_REVIEW);
            contentRepo.save(content);
        }

        // Apply the decision and transition state
        String decision = dto.getDecision().toUpperCase();
        switch (decision) {
            case "APPROVED" -> content.setStatus(ContentStatus.APPROVED);
            case "REJECTED" -> content.setStatus(ContentStatus.REJECTED);
            case "FLAGGED"  -> content.setStatus(ContentStatus.FLAGGED);
            default -> throw new RuntimeException("Invalid decision: " + decision +
                    ". Valid values: APPROVED, REJECTED, FLAGGED");
        }
        contentRepo.save(content);

        // Persist review record (GRASP – Creator: Moderator creates Review)
        Review review = new Review(decision, dto.getComments(), content, moderator);
        review = reviewRepo.save(review);

        // Notify the content creator
        ContentCreator creator = content.getCreator();
        if (creator != null) {
            String msg = String.format(
                "Your content \"%s\" has been %s by moderator %s. %s",
                content.getTitle(), decision, moderator.getName(),
                dto.getComments() != null ? "Comment: " + dto.getComments() : "");
            notificationRepo.save(new Notification(msg, creator));
        }

        return review;
    }

    private Content findContent(ReviewDecisionDTO dto) {
        if (dto.getContentId() != null) {
            return contentRepo.findById(dto.getContentId())
                    .orElseThrow(() -> new RuntimeException("Content not found: " + dto.getContentId()));
        }
        if (dto.getContentTitle() != null && !dto.getContentTitle().isBlank()) {
            return contentRepo.findByTitleIgnoreCase(dto.getContentTitle()).stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Content not found: " + dto.getContentTitle()));
        }
        throw new RuntimeException("Content id or title is required for review submission");
    }

    @Override
    public List<Review> getReviewsByContent(Long contentId) {
        Content content = contentRepo.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found: " + contentId));
        return reviewRepo.findByContent(content);
    }

    @Override
    public List<Review> getReviewsByModerator(Long moderatorId) {
        Moderator moderator = moderatorRepo.findById(moderatorId)
                .orElseThrow(() -> new RuntimeException("Moderator not found: " + moderatorId));
        return reviewRepo.findByModerator(moderator);
    }

    @Override
    public List<Review> getReviewsForCreator(Long creatorId) {
        ContentCreator creator = creatorRepo.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Content creator not found: " + creatorId));
        List<Content> creatorContents = contentRepo.findByCreator(creator);
        if (creatorContents.isEmpty()) {
            return List.of();
        }
        return reviewRepo.findByContentIn(creatorContents);
    }
}
