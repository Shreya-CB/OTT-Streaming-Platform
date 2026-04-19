package com.ott.platform.service;

import com.ott.platform.model.Content;
import com.ott.platform.model.Viewer;
import com.ott.platform.pattern.strategy.RecommendationStrategy;
import com.ott.platform.repository.ViewerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Context class for the Strategy Pattern.
 * At runtime, callers can switch between "genre" and "ml" algorithms
 * without changing any other code (OCP).
 */
@Service
public class RecommendationService {

    private final ViewerRepository         viewerRepo;
    private final RecommendationStrategy   genreStrategy;
    private final RecommendationStrategy   mlStrategy;

    @Autowired
    public RecommendationService(ViewerRepository viewerRepo,
                                  @Qualifier("genreRecommendation") RecommendationStrategy genreStrategy,
                                  @Qualifier("mlRecommendation")    RecommendationStrategy mlStrategy) {
        this.viewerRepo    = viewerRepo;
        this.genreStrategy = genreStrategy;
        this.mlStrategy    = mlStrategy;
    }

    /**
     * Returns recommendations for a viewer using the chosen algorithm.
     * @param algorithm "genre" | "ml"
     */
    public List<Content> recommend(Long viewerId, String algorithm) {
        Viewer viewer = viewerRepo.findById(viewerId)
                .orElseThrow(() -> new RuntimeException("Viewer not found: " + viewerId));

        RecommendationStrategy strategy = "ml".equalsIgnoreCase(algorithm)
                ? mlStrategy : genreStrategy;

        return strategy.recommend(viewer);
    }
}
