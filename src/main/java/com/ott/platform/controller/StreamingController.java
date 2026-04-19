package com.ott.platform.controller;

import com.ott.platform.dto.RatingDTO;
import com.ott.platform.dto.RatingResponseDTO;
import com.ott.platform.dto.ContentSummaryDTO;
import com.ott.platform.dto.RatingDTO;
import com.ott.platform.dto.RatingResponseDTO;
import com.ott.platform.dto.WatchHistoryDTO;
import com.ott.platform.dto.WatchlistDTO;
import com.ott.platform.model.*;
import com.ott.platform.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * MVC – Controller for the streaming, watchlist, and recommendation use cases.
 * Use Cases: Stream content, View watch history, Manage watchlist,
 *            Rate content, Get recommendations.
 *
 * Base URL: /api/stream
 */
@RestController
@RequestMapping("/api/stream")
@CrossOrigin(origins = "*")
public class StreamingController {

    private final WatchService           watchService;
    private final RecommendationService  recommendationService;

    @Autowired
    public StreamingController(WatchService watchService,
                                RecommendationService recommendationService) {
        this.watchService          = watchService;
        this.recommendationService = recommendationService;
    }

    /** POST /api/stream/watch – Viewer streams a content item */
    @PostMapping("/watch")
    public ResponseEntity<?> streamContent(@RequestParam Long viewerId,
                                            @RequestParam Long contentId) {
        try {
            WatchHistory wh = watchService.streamContent(viewerId, contentId);
            return ResponseEntity.ok(Map.of(
                "message", "Streaming started",
                "watchHistory", WatchHistoryDTO.fromWatchHistory(wh)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/stream/history/{viewerId} – View watch history */
    @GetMapping("/history/{viewerId}")
    public ResponseEntity<List<WatchHistoryDTO>> getHistory(@PathVariable Long viewerId) {
        return ResponseEntity.ok(watchService.getWatchHistory(viewerId).stream()
                .map(WatchHistoryDTO::fromWatchHistory)
                .toList());
    }

    // ── Watchlist ─────────────────────────────────────────────────────────────

    /** GET /api/stream/watchlist/{viewerId} */
    @GetMapping("/watchlist/{viewerId}")
    public ResponseEntity<WatchlistDTO> getWatchlist(@PathVariable Long viewerId) {
        return ResponseEntity.ok(WatchlistDTO.fromWatchlist(watchService.getWatchlist(viewerId)));
    }

    /** POST /api/stream/watchlist/{viewerId}/add?contentId=x */
    @PostMapping("/watchlist/{viewerId}/add")
    public ResponseEntity<?> addToWatchlist(@PathVariable Long viewerId,
                                             @RequestParam Long contentId) {
        try {
            return ResponseEntity.ok(watchService.addToWatchlist(viewerId, contentId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** DELETE /api/stream/watchlist/{viewerId}/remove?contentId=x */
    @DeleteMapping("/watchlist/{viewerId}/remove")
    public ResponseEntity<?> removeFromWatchlist(@PathVariable Long viewerId,
                                                  @RequestParam Long contentId) {
        try {
            return ResponseEntity.ok(watchService.removeFromWatchlist(viewerId, contentId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Ratings ───────────────────────────────────────────────────────────────

    /** POST /api/stream/rate – Viewer rates content by title (contentId hidden) */
    @PostMapping("/rate")
    public ResponseEntity<?> rateContent(@Valid @RequestBody RatingDTO dto) {
        try {
            RatingReview rr = watchService.rateContent(
                    dto.getViewerId(), dto.getContentTitle(), dto.getRating(), dto.getComment());
            RatingResponseDTO response = new RatingResponseDTO(
                    rr.getContent().getTitle(),
                    rr.getViewer().getName(),
                    rr.getRating(),
                    rr.getComment());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/stream/content/{contentId}/ratings */
    @GetMapping("/content/{contentId}/ratings")
    public ResponseEntity<?> getRatings(@PathVariable Long contentId) {
        return ResponseEntity.ok(Map.of(
            "ratings",       watchService.getRatings(contentId),
            "averageRating", watchService.getAverageRating(contentId)));
    }

    // ── Recommendations ───────────────────────────────────────────────────────

    /** GET /api/stream/recommendations/{viewerId}?algorithm=genre|ml */
    @GetMapping("/recommendations/{viewerId}")
    public ResponseEntity<?> getRecommendations(
            @PathVariable Long viewerId,
            @RequestParam(defaultValue = "genre") String algorithm) {
        try {
            List<Content> recs = recommendationService.recommend(viewerId, algorithm);
            return ResponseEntity.ok(Map.of(
                    "algorithm", algorithm,
                    "recommendations", recs.stream()
                            .map(ContentSummaryDTO::fromContent)
                            .toList()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
