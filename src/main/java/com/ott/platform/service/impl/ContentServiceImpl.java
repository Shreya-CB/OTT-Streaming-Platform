package com.ott.platform.service.impl;

import com.ott.platform.dto.ContentResubmitDTO;
import com.ott.platform.dto.ContentUploadDTO;
import com.ott.platform.model.*;
import com.ott.platform.pattern.facade.ContentManagementFacade;
import com.ott.platform.repository.*;
import com.ott.platform.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Manages content lifecycle state transitions.
 *
 * State machine (mirrors Content Upload & Moderation State Diagram):
 *   DRAFT → SUBMITTED  (uploadContent via Facade)
 *   SUBMITTED → UNDER_REVIEW  (ModerationService)
 *   UNDER_REVIEW → APPROVED | REJECTED | FLAGGED  (ModerationService)
 *   APPROVED → PUBLISHED  (publishContent)
 *   REJECTED → SUBMITTED  (resubmitContent – Content Creator resubmits)
 *   PUBLISHED → DRAFT     (archiveContent)
 *
 * SOLID – SRP: only content lifecycle logic here.
 * GRASP – Pure Fabrication: this class handles logic not owned by any entity.
 */
@Service
public class ContentServiceImpl implements ContentService {

    private final ContentRepository        contentRepo;
    private final ContentCreatorRepository creatorRepo;
    private final ContentManagementFacade  facade;

    @Autowired
    public ContentServiceImpl(ContentRepository contentRepo,
                               ContentCreatorRepository creatorRepo,
                               ContentManagementFacade facade) {
        this.contentRepo = contentRepo;
        this.creatorRepo = creatorRepo;
        this.facade      = facade;
    }

    /** Delegates to Facade (Facade Pattern) – handles create + metadata + notify. */
    @Override
    public Content uploadContent(ContentUploadDTO dto) {
        return facade.uploadAndSubmit(dto);
    }

    @Override
    public Content findById(Long id) {
        return contentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found: " + id));
    }

    @Override
    public List<Content> findAll() {
        return contentRepo.findAll();
    }

    @Override
    public List<Content> findByStatus(ContentStatus status) {
        return contentRepo.findByStatus(status);
    }

    @Override
    public List<Content> searchByTitle(String keyword) {
        return contentRepo.searchByTitle(keyword);
    }

    @Override
    public List<Content> findByCreatorId(Long creatorId) {
        ContentCreator creator = creatorRepo.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Creator not found: " + creatorId));
        return contentRepo.findByCreator(creator);
    }

    @Override
    public List<Content> findPublishedByGenre(String genre) {
        return contentRepo.findPublishedByGenre(genre);
    }

    /** Transition: APPROVED → PUBLISHED */
    @Override
    public Content publishContent(Long contentId) {
        Content content = findById(contentId);
        if (content.getStatus() != ContentStatus.APPROVED) {
            throw new RuntimeException(
                "Cannot publish content with status: " + content.getStatus() +
                ". Content must be APPROVED first.");
        }
        content.setStatus(ContentStatus.PUBLISHED);
        return contentRepo.save(content);
    }

    /** Transition: PUBLISHED → DRAFT (soft archive / takedown) */
    @Override
    public Content archiveContent(Long contentId) {
        Content content = findById(contentId);
        content.setStatus(ContentStatus.DRAFT);
        return contentRepo.save(content);
    }

    /** Transition: REJECTED | FLAGGED → SUBMITTED (creator resubmits after modification) */
    @Override
    public Content resubmitContent(Long contentId) {
        Content content = findById(contentId);
        if (content.getStatus() != ContentStatus.REJECTED && content.getStatus() != ContentStatus.FLAGGED) {
            throw new RuntimeException(
                "Only REJECTED or FLAGGED content can be resubmitted. Current status: " + content.getStatus());
        }
        content.setStatus(ContentStatus.SUBMITTED);
        return contentRepo.save(content);
    }

    @Override
    public Content resubmitContent(Long contentId, ContentResubmitDTO dto) {
        Content content = findById(contentId);
        if (content.getStatus() != ContentStatus.REJECTED && content.getStatus() != ContentStatus.FLAGGED) {
            throw new RuntimeException(
                "Only REJECTED or FLAGGED content can be resubmitted. Current status: " + content.getStatus());
        }

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            content.setTitle(dto.getTitle());
        }

        Metadata metadata = content.getMetadata() != null ? content.getMetadata() : new Metadata();
        if (dto.getGenre() != null) {
            metadata.setGenre(dto.getGenre());
        }
        if (dto.getDuration() > 0) {
            metadata.setDuration(dto.getDuration());
        }
        if (dto.getAgeRating() != null) {
            metadata.setAgeRating(dto.getAgeRating());
        }
        if (dto.getLanguage() != null) {
            metadata.setLanguage(dto.getLanguage());
        }
        if (dto.getDescription() != null) {
            metadata.setDescription(dto.getDescription());
        }
        content.setMetadata(metadata);
        content.setStatus(ContentStatus.SUBMITTED);
        return contentRepo.save(content);
    }

    @Override
    public void deleteContent(Long contentId) {
        contentRepo.deleteById(contentId);
    }
}
