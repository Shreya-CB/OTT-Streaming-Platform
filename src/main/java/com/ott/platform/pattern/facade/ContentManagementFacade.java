package com.ott.platform.pattern.facade;

import com.ott.platform.dto.ContentUploadDTO;
import com.ott.platform.model.*;
import com.ott.platform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ╔═══════════════════════════════════════════════════════════╗
 * ║  DESIGN PATTERN 3: FACADE (Structural)                   ║
 * ║  Provides a single simplified entry-point for the        ║
 * ║  multi-step content upload workflow:                      ║
 * ║    1. Create Content entity                              ║
 * ║    2. Attach Metadata                                    ║
 * ║    3. Transition state → SUBMITTED                       ║
 * ║    4. Notify moderators                                  ║
 * ║  Hides complexity from ContentController.                ║
 * ╚═══════════════════════════════════════════════════════════╝
 */
@Component
public class ContentManagementFacade {

    private final ContentRepository      contentRepo;
    private final ContentCreatorRepository creatorRepo;
    private final MetadataRepository     metadataRepo;
    private final NotificationRepository notificationRepo;
    private final ModeratorRepository    moderatorRepo;

    @Autowired
    public ContentManagementFacade(ContentRepository contentRepo,
                                    ContentCreatorRepository creatorRepo,
                                    MetadataRepository metadataRepo,
                                    NotificationRepository notificationRepo,
                                    ModeratorRepository moderatorRepo) {
        this.contentRepo      = contentRepo;
        this.creatorRepo      = creatorRepo;
        this.metadataRepo     = metadataRepo;
        this.notificationRepo = notificationRepo;
        this.moderatorRepo    = moderatorRepo;
    }

    /**
     * Orchestrates the full content upload flow in one call.
     * Content Controller simply calls this — it doesn't need to know
     * about metadata persistence, state transitions, or notifications.
     */
    public Content uploadAndSubmit(ContentUploadDTO dto) {

        // Step 1 – Find the creator
        ContentCreator creator = creatorRepo.findById(dto.getCreatorId())
                .orElseThrow(() -> new RuntimeException(
                        "ContentCreator not found: " + dto.getCreatorId()));

        // Step 2 – Build and save the Content in DRAFT state
        Content content = new Content(dto.getTitle(), creator);
        content = contentRepo.save(content);

        // Step 3 – Build and attach Metadata
        Metadata metadata = new Metadata(
                dto.getGenre(), dto.getDuration(),
                dto.getAgeRating(), dto.getLanguage(),
                dto.getDescription());
        metadata = metadataRepo.save(metadata);
        content.setMetadata(metadata);

        // Step 4 – Transition: DRAFT → SUBMITTED
        content.setStatus(ContentStatus.SUBMITTED);
        content = contentRepo.save(content);

        // Step 5 – Notify all moderators
        final Content savedContent = content;
        moderatorRepo.findAll().forEach(mod -> {
            Notification n = new Notification("New content submitted for review: " + savedContent.getTitle() + "", mod);
            notificationRepo.save(n);
        });

        return content;
    }
}
