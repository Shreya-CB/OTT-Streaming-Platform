package com.ott.platform.service;

import com.ott.platform.dto.ContentUploadDTO;
import com.ott.platform.model.Content;
import com.ott.platform.model.ContentStatus;
import java.util.List;

/**
 * SRP – content lifecycle operations only.
 * publish() and archive() live here (not in the Content entity) per SOLID/SRP.
 */
public interface ContentService {
    Content uploadContent(ContentUploadDTO dto);          // uses Facade internally
    Content findById(Long id);
    List<Content> findAll();
    List<Content> findByStatus(ContentStatus status);
    List<Content> searchByTitle(String keyword);
    List<Content> findByCreatorId(Long creatorId);
    List<Content> findPublishedByGenre(String genre);
    Content publishContent(Long contentId);               // APPROVED → PUBLISHED
    Content archiveContent(Long contentId);               // PUBLISHED → DRAFT (soft archive)
    Content resubmitContent(Long contentId);              // REJECTED → SUBMITTED
    Content resubmitContent(Long contentId, com.ott.platform.dto.ContentResubmitDTO dto); // edit and resubmit
    void deleteContent(Long contentId);
}
