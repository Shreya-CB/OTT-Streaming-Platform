package com.ott.platform.service;

import com.ott.platform.model.*;
import java.util.List;

public interface WatchService {
    WatchHistory streamContent(Long viewerId, Long contentId);
    List<WatchHistory> getWatchHistory(Long viewerId);
    Watchlist addToWatchlist(Long viewerId, Long contentId);
    Watchlist removeFromWatchlist(Long viewerId, Long contentId);
    Watchlist getWatchlist(Long viewerId);
    RatingReview rateContent(Long viewerId, String contentTitle, int rating, String comment);
    List<RatingReview> getRatings(Long contentId);
    Double getAverageRating(Long contentId);
}
