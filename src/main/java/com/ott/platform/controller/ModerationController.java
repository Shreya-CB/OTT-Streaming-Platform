package com.ott.platform.controller;

import com.ott.platform.dto.ModeratorQueueItemDTO;
import com.ott.platform.dto.ReviewDecisionDTO;
import com.ott.platform.dto.ReviewSummaryDTO;
import com.ott.platform.model.Content;
import com.ott.platform.model.ContentStatus;
import com.ott.platform.model.Review;
import com.ott.platform.service.ContentService;
import com.ott.platform.service.ModerationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * MVC – Controller for Moderation use cases.
 * Use Cases: Review content, Approve content, Reject content, Flag policy violation.
 *
 * Base URL: /api/moderation
 */
@RestController
@RequestMapping("/api/moderation")
@CrossOrigin(origins = "*")
public class ModerationController {

    private final ModerationService moderationService;
    private final ContentService    contentService;

    @Autowired
    public ModerationController(ModerationService moderationService,
                                ContentService contentService) {
        this.moderationService = moderationService;
        this.contentService    = contentService;
    }

    /** POST /api/moderation/review – Moderator submits a review decision */
    @PostMapping("/review")
    public ResponseEntity<?> reviewContent(@Valid @RequestBody ReviewDecisionDTO dto) {
        try {
            Review review = moderationService.reviewContent(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapReviewSummary(review));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/moderation/queue?status=SUBMITTED|UNDER_REVIEW – View moderation queue with description */
    @GetMapping("/queue")
    public ResponseEntity<List<ModeratorQueueItemDTO>> getReviewQueue(
            @RequestParam(defaultValue = "UNDER_REVIEW") String status) {
        ContentStatus contentStatus = ContentStatus.valueOf(status.toUpperCase());
        List<Content> queue = contentService.findByStatus(contentStatus);
        List<ModeratorQueueItemDTO> items = queue.stream()
                .map(this::mapQueueItem)
                .toList();
        return ResponseEntity.ok(items);
    }

    /** GET /api/moderation/content/{contentId} – All reviews for a content */
    @GetMapping("/content/{contentId}")
    public ResponseEntity<List<ReviewSummaryDTO>> getReviewsByContent(@PathVariable Long contentId) {
        List<ReviewSummaryDTO> reviews = moderationService.getReviewsByContent(contentId).stream()
                .map(this::mapReviewSummary)
                .toList();
        return ResponseEntity.ok(reviews);
    }

    /** GET /api/moderation/moderator/{moderatorId} – All reviews by a moderator */
    @GetMapping("/moderator/{moderatorId}")
    public ResponseEntity<List<ReviewSummaryDTO>> getReviewsByModerator(@PathVariable Long moderatorId) {
        List<ReviewSummaryDTO> reviews = moderationService.getReviewsByModerator(moderatorId).stream()
                .map(this::mapReviewSummary)
                .toList();
        return ResponseEntity.ok(reviews);
    }

    private ReviewSummaryDTO mapReviewSummary(Review review) {
        return new ReviewSummaryDTO(
                review.getContent().getTitle(),
                review.getModerator().getName(),
                review.getDecision(),
                review.getComments(),
                review.getReviewedAt());
    }

    private ModeratorQueueItemDTO mapQueueItem(Content content) {
        return new ModeratorQueueItemDTO(
                content.getTitle(),
                content.getMetadata() != null ? content.getMetadata().getDescription() : null,
                content.getMetadata() != null ? content.getMetadata().getGenre() : null,
                content.getCreator() != null ? content.getCreator().getName() : null,
                content.getStatus().name());
    }
}
