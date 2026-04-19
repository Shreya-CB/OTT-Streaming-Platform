package com.ott.platform.repository;
import com.ott.platform.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByViewer(Viewer viewer);
    Optional<Subscription> findTopByViewerAndStatusOrderByStartDateDesc(Viewer viewer, SubscriptionStatus status);
}
