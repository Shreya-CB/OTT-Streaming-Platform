package com.ott.platform.repository;
import com.ott.platform.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
    List<WatchHistory> findByViewer(Viewer viewer);
    Optional<WatchHistory> findByViewerAndContent(Viewer viewer, Content content);
}
