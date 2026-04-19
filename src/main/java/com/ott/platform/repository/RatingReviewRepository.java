package com.ott.platform.repository;
import com.ott.platform.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public interface RatingReviewRepository extends JpaRepository<RatingReview, Long> {
    List<RatingReview> findByContent(Content content);
    Optional<RatingReview> findByViewerAndContent(Viewer viewer, Content content);
    @Query("SELECT AVG(r.rating) FROM RatingReview r WHERE r.content = :content")
    Double averageRatingByContent(@Param("content") Content content);
}
