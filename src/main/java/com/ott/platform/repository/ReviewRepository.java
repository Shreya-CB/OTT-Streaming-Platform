package com.ott.platform.repository;
import com.ott.platform.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByContent(Content content);
    List<Review> findByContentIn(List<Content> contents);
    List<Review> findByModerator(Moderator moderator);
}
