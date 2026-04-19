package com.ott.platform.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ModeratorQueueItemDTO {
    private String contentTitle;
    private String description;
    private String genre;
    private String creatorName;
    private String status;
}
