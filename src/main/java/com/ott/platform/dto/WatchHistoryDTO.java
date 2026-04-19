package com.ott.platform.dto;

import com.ott.platform.model.WatchHistory;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class WatchHistoryDTO {
    private ContentSummaryDTO content;
    private LocalDate lastWatchedDate;

    public static WatchHistoryDTO fromWatchHistory(WatchHistory history) {
        return new WatchHistoryDTO(
                ContentSummaryDTO.fromContent(history.getContent()),
                history.getLastWatchedDate());
    }
}
