package com.ott.platform.service.impl;

import com.ott.platform.model.*;
import com.ott.platform.repository.*;
import com.ott.platform.service.WatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

/**
 * Implements the Content Streaming & Interaction Activity Diagram:
 *   Login → Browse/Search → Select → Validate Subscription
 *   → Grant Access → Start Streaming → Update Watch History
 *   → (optionally) Add to Watchlist | Rate | Write Review
 */
@Service
public class WatchServiceImpl implements WatchService {

    private final ViewerRepository       viewerRepo;
    private final ContentRepository      contentRepo;
    private final WatchHistoryRepository watchHistoryRepo;
    private final WatchlistRepository    watchlistRepo;
    private final RatingReviewRepository ratingRepo;
    private final SubscriptionRepository subRepo;

    @Autowired
    public WatchServiceImpl(ViewerRepository viewerRepo,
                             ContentRepository contentRepo,
                             WatchHistoryRepository watchHistoryRepo,
                             WatchlistRepository watchlistRepo,
                             RatingReviewRepository ratingRepo,
                             SubscriptionRepository subRepo) {
        this.viewerRepo       = viewerRepo;
        this.contentRepo      = contentRepo;
        this.watchHistoryRepo = watchHistoryRepo;
        this.watchlistRepo    = watchlistRepo;
        this.ratingRepo       = ratingRepo;
        this.subRepo          = subRepo;
    }

    /**
     * Records a streaming session.
     * Validates that viewer has an ACTIVE subscription before granting access.
     */
    @Override
    public WatchHistory streamContent(Long viewerId, Long contentId) {
        Viewer viewer = viewerRepo.findById(viewerId)
                .orElseThrow(() -> new RuntimeException("Viewer not found: " + viewerId));
        Content content = contentRepo.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found: " + contentId));

        if (content.getStatus() != ContentStatus.PUBLISHED) {
            throw new RuntimeException("Content is not available for streaming.");
        }

        // Validate subscription (from Activity Diagram – Streaming)
        boolean hasActive = subRepo.findTopByViewerAndStatusOrderByStartDateDesc(
                viewer, SubscriptionStatus.ACTIVE).isPresent();
        if (!hasActive) {
            throw new RuntimeException("No active subscription. Please subscribe to stream content.");
        }

        // Update or create watch history record
        WatchHistory history = watchHistoryRepo.findByViewerAndContent(viewer, content)
                .orElse(new WatchHistory(viewer, content));
        history.setLastWatchedDate(LocalDate.now());
        return watchHistoryRepo.save(history);
    }

    @Override
    public List<WatchHistory> getWatchHistory(Long viewerId) {
        Viewer viewer = viewerRepo.findById(viewerId)
                .orElseThrow(() -> new RuntimeException("Viewer not found: " + viewerId));
        return watchHistoryRepo.findByViewer(viewer);
    }

    @Override
    public Watchlist addToWatchlist(Long viewerId, Long contentId) {
        Viewer  viewer  = viewerRepo.findById(viewerId)
                .orElseThrow(() -> new RuntimeException("Viewer not found: " + viewerId));
        Content content = contentRepo.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found: " + contentId));

        Watchlist watchlist = watchlistRepo.findByViewer(viewer)
                .orElse(new Watchlist(viewer));

        if (!watchlist.getContents().contains(content)) {
            watchlist.getContents().add(content);
        }
        return watchlistRepo.save(watchlist);
    }

    @Override
    public Watchlist removeFromWatchlist(Long viewerId, Long contentId) {
        Viewer  viewer  = viewerRepo.findById(viewerId)
                .orElseThrow(() -> new RuntimeException("Viewer not found: " + viewerId));
        Content content = contentRepo.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found: " + contentId));

        Watchlist watchlist = watchlistRepo.findByViewer(viewer)
                .orElseThrow(() -> new RuntimeException("Watchlist not found for viewer: " + viewerId));
        watchlist.getContents().removeIf(c -> c.getContentId().equals(contentId));
        return watchlistRepo.save(watchlist);
    }

    @Override
    public Watchlist getWatchlist(Long viewerId) {
        Viewer viewer = viewerRepo.findById(viewerId)
                .orElseThrow(() -> new RuntimeException("Viewer not found: " + viewerId));
        return watchlistRepo.findByViewer(viewer)
                .orElse(new Watchlist(viewer));
    }

    @Override
    public RatingReview rateContent(Long viewerId, String contentTitle, int rating, String comment) {
        Viewer  viewer  = viewerRepo.findById(viewerId)
                .orElseThrow(() -> new RuntimeException("Viewer not found: " + viewerId));
        Content content = findContentByTitle(contentTitle);

        // Upsert: if already rated, update
        RatingReview rr = ratingRepo.findByViewerAndContent(viewer, content)
                .orElse(new RatingReview(rating, comment, viewer, content));
        rr.setRating(rating);
        rr.setComment(comment);
        return ratingRepo.save(rr);
    }

    private Content findContentByTitle(String title) {
        return contentRepo.findByTitleIgnoreCase(title).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Content not found: " + title));
    }

    @Override
    public List<RatingReview> getRatings(Long contentId) {
        Content content = contentRepo.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found: " + contentId));
        return ratingRepo.findByContent(content);
    }

    @Override
    public Double getAverageRating(Long contentId) {
        Content content = contentRepo.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found: " + contentId));
        return ratingRepo.averageRatingByContent(content);
    }
}
