package com.ott.platform.pattern.strategy;

import com.ott.platform.model.*;
import com.ott.platform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Recommends published content based on genres the viewer has already watched.
 * Implements RecommendationStrategy (Strategy pattern – Behavioral).
 */
@Component("genreRecommendation")
public class GenreBasedRecommendation implements RecommendationStrategy {

    private final WatchHistoryRepository watchHistoryRepo;
    private final ContentRepository      contentRepo;

    @Autowired
    public GenreBasedRecommendation(WatchHistoryRepository watchHistoryRepo,
                                     ContentRepository contentRepo) {
        this.watchHistoryRepo = watchHistoryRepo;
        this.contentRepo      = contentRepo;
    }

    @Override
    public List<Content> recommend(Viewer viewer) {
        // 1. Collect genres from the viewer's watch history
        List<WatchHistory> history = watchHistoryRepo.findByViewer(viewer);

        Set<String> watchedGenres = history.stream()
                .map(wh -> wh.getContent().getMetadata())
                .filter(Objects::nonNull)
                .map(Metadata::getGenre)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (watchedGenres.isEmpty()) {
            // Fall back: return latest 10 published items the viewer has not already watched
            List<Content> published = contentRepo.findByStatus(ContentStatus.PUBLISHED);
            Set<Long> watchedIds = history.stream()
                    .map(wh -> wh.getContent().getContentId())
                    .collect(Collectors.toSet());
            return published.stream()
                    .filter(c -> !watchedIds.contains(c.getContentId()))
                    .limit(10)
                    .collect(Collectors.toList());
        }

        // 2. Find published content in those genres, excluding already watched
        Set<Long> watchedIds = history.stream()
                .map(wh -> wh.getContent().getContentId())
                .collect(Collectors.toSet());

        List<Content> recommendations = watchedGenres.stream()
                .flatMap(genre -> contentRepo.findPublishedByGenre(genre).stream())
                .filter(c -> !watchedIds.contains(c.getContentId()))
                .distinct()
                .limit(10)
                .collect(Collectors.toList());

        if (!recommendations.isEmpty()) {
            return recommendations;
        }

        List<Content> fallback = contentRepo.findByStatus(ContentStatus.PUBLISHED).stream()
                .filter(c -> !watchedIds.contains(c.getContentId()))
                .limit(10)
                .collect(Collectors.toList());
        if (!fallback.isEmpty()) {
            return fallback;
        }

        return contentRepo.findByStatus(ContentStatus.PUBLISHED)
                .stream().limit(10).collect(Collectors.toList());
    }
}
