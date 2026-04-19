package com.ott.platform.dto;

import com.ott.platform.model.Content;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ContentSummaryDTO {
    private Long   contentId;
    private String title;
    private String description;
    private String genre;
    private Integer duration;
    private String language;
    private String status;
    private String creatorName;

    public static ContentSummaryDTO fromContent(Content content) {
        String description = null;
        String genre = null;
        Integer duration = null;
        String language = null;
        if (content.getMetadata() != null) {
            description = content.getMetadata().getDescription();
            genre = content.getMetadata().getGenre();
            duration = content.getMetadata().getDuration();
            language = content.getMetadata().getLanguage();
        }
        return new ContentSummaryDTO(
                content.getContentId(),
                content.getTitle(),
                description,
                genre,
                duration,
                language,
                content.getStatus() != null ? content.getStatus().name() : null,
                content.getCreator() != null ? content.getCreator().getName() : null);
    }
}
