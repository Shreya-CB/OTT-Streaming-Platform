package com.ott.platform.pattern.strategy;

import com.ott.platform.model.*;
import com.ott.platform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Recommends content with the highest average rating that the viewer has not yet watched.
 * Simulates an ML-based ranking approach (extensible to a real model call).
 * Implements RecommendationStrategy (Strategy pattern – Behavioral).
 */
@Component("mlRecommendation")
public class MLRecommendation implements RecommendationStrategy {

    private final ContentRepository      contentRepo;
    private final RatingReviewRepository ratingRepo;
    private final WatchHistoryRepository watchHistoryRepo;

    @Autowired
    public MLRecommendation(ContentRepository contentRepo,
                             RatingReviewRepository ratingRepo,
                             WatchHistoryRepository watchHistoryRepo) {
        this.contentRepo      = contentRepo;
        this.ratingRepo       = ratingRepo;
        this.watchHistoryRepo = watchHistoryRepo;
    }

    @Override
    public List<Content> recommend(Viewer viewer) {
        Set<Long> watchedIds = watchHistoryRepo.findByViewer(viewer).stream()
                .map(wh -> wh.getContent().getContentId())
                .collect(Collectors.toSet());

        List<Content> published = contentRepo.findByStatus(ContentStatus.PUBLISHED);
        List<Content> unwatched = published.stream()
                .filter(c -> !watchedIds.contains(c.getContentId()))
                .collect(Collectors.toList());

        if (unwatched.isEmpty()) {
            return published.stream().limit(10).collect(Collectors.toList());
        }

        // Score each unwatched item by its average rating (0 if unrated)
        List<Content> recommendations = unwatched.stream()
                .sorted(Comparator.comparingDouble(c -> {
                    Double avg = ratingRepo.averageRatingByContent(c);
                    return avg == null ? 0.0 : -avg;   // descending
                }))
                .limit(10)
                .collect(Collectors.toList());

        if (!recommendations.isEmpty()) {
            return recommendations;
        }

        return published.stream().limit(10).collect(Collectors.toList());
    }
}
