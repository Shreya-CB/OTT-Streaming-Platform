package com.ott.platform.controller;

import com.ott.platform.dto.ContentResubmitDTO;
import com.ott.platform.dto.ContentSummaryDTO;
import com.ott.platform.dto.ContentUploadDTO;
import com.ott.platform.dto.ReviewSummaryDTO;
import com.ott.platform.model.*;
import com.ott.platform.service.ContentService;
import com.ott.platform.service.ModerationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * MVC – Controller layer for Content operations.
 * GRASP – Controller: coordinates ContentService (no business logic here).
 *
 * Use Cases covered:
 *   - Upload content (ContentCreator)
 *   - Browse & search content (Viewer)
 *   - View content status (ContentCreator)
 *   - Publish content (after Approved)
 *   - Archive / Resubmit content
 *
 * Base URL: /api/content
 */
@RestController
@RequestMapping("/api/content")
@CrossOrigin(origins = "*")
public class ContentController {

    private final ContentService contentService;
    private final ModerationService moderationService;

    @Autowired
    public ContentController(ContentService contentService,
                              ModerationService moderationService) {
        this.contentService     = contentService;
        this.moderationService  = moderationService;
    }

    /** POST /api/content/upload – ContentCreator uploads content */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadContent(@Valid @RequestBody ContentUploadDTO dto) {
        try {
            Content content = contentService.uploadContent(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ContentSummaryDTO.fromContent(content));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/content – Browse all content (filter by status optional) */
    @GetMapping
    public ResponseEntity<List<ContentSummaryDTO>> getAllContent(
            @RequestParam(required = false) String status) {
        List<Content> contents = status != null
                ? contentService.findByStatus(ContentStatus.valueOf(status.toUpperCase()))
                : contentService.findAll();
        return ResponseEntity.ok(contents.stream()
                .map(ContentSummaryDTO::fromContent)
                .toList());
    }

    /** GET /api/content/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<?> getContentById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ContentSummaryDTO.fromContent(contentService.findById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/content/search?keyword=xyz – Search by title */
    @GetMapping("/search")
    public ResponseEntity<List<ContentSummaryDTO>> searchContent(@RequestParam String keyword) {
        return ResponseEntity.ok(contentService.searchByTitle(keyword).stream()
                .map(ContentSummaryDTO::fromContent)
                .toList());
    }

    /** GET /api/content/creator/{creatorId} – View creator's uploads */
    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<ContentSummaryDTO>> getByCreator(@PathVariable Long creatorId) {
        return ResponseEntity.ok(contentService.findByCreatorId(creatorId).stream()
                .map(ContentSummaryDTO::fromContent)
                .toList());
    }

    /** GET /api/content/creator/{creatorId}/feedback – Content creator sees moderator reviews/comments */
    @GetMapping("/creator/{creatorId}/feedback")
    public ResponseEntity<List<ReviewSummaryDTO>> getCreatorFeedback(@PathVariable Long creatorId) {
        List<ReviewSummaryDTO> feedback = moderationService.getReviewsForCreator(creatorId)
                .stream()
                .map(this::mapReviewSummary)
                .toList();
        return ResponseEntity.ok(feedback);
    }

    /** GET /api/content/genre/{genre} – Browse published content by genre */
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<ContentSummaryDTO>> getByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(contentService.findPublishedByGenre(genre).stream()
                .map(ContentSummaryDTO::fromContent)
                .toList());
    }

    private ReviewSummaryDTO mapReviewSummary(com.ott.platform.model.Review review) {
        return new ReviewSummaryDTO(
                review.getContent().getTitle(),
                review.getModerator().getName(),
                review.getDecision(),
                review.getComments(),
                review.getReviewedAt());
    }

    /** POST /api/content/{id}/publish – Transition APPROVED → PUBLISHED */
    @PostMapping("/{id}/publish")
    public ResponseEntity<?> publishContent(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ContentSummaryDTO.fromContent(contentService.publishContent(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** POST /api/content/{id}/archive – Archive (takedown) published content */
    @PostMapping("/{id}/archive")
    public ResponseEntity<?> archiveContent(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ContentSummaryDTO.fromContent(contentService.archiveContent(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** POST /api/content/{id}/resubmit – ContentCreator resubmits after rejection */
    @PostMapping("/{id}/resubmit")
    public ResponseEntity<?> resubmitContent(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ContentSummaryDTO.fromContent(contentService.resubmitContent(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PUT /api/content/{id}/resubmit – ContentCreator edits and resubmits flagged/rejected content */
    @PutMapping("/{id}/resubmit")
    public ResponseEntity<?> resubmitContent(@PathVariable Long id,
                                             @Valid @RequestBody ContentResubmitDTO dto) {
        try {
            return ResponseEntity.ok(ContentSummaryDTO.fromContent(contentService.resubmitContent(id, dto)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** DELETE /api/content/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContent(@PathVariable Long id) {
        contentService.deleteContent(id);
        return ResponseEntity.ok(Map.of("message", "Content deleted"));
    }
}
