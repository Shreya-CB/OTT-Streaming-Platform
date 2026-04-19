package com.ott.platform.repository;
import com.ott.platform.model.ContentCreator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface ContentCreatorRepository extends JpaRepository<ContentCreator, Long> {
    Optional<ContentCreator> findByEmail(String email);
}
