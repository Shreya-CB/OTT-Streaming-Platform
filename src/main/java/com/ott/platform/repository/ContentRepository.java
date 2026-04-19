package com.ott.platform.repository;
import com.ott.platform.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findByStatus(ContentStatus status);
    List<Content> findByCreator(ContentCreator creator);
    @Query("SELECT c FROM Content c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%',:kw,'%'))")
    List<Content> searchByTitle(@Param("kw") String keyword);
    List<Content> findByTitleIgnoreCase(String title);
    @Query("SELECT c FROM Content c WHERE c.metadata.genre = :genre AND c.status = 'PUBLISHED'")
    List<Content> findPublishedByGenre(@Param("genre") String genre);
}
