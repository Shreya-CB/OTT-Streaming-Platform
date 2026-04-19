package com.ott.platform.dto;

import com.ott.platform.model.Watchlist;
import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class WatchlistDTO {
    private String viewerName;
    private List<ContentSummaryDTO> contents;

    public static WatchlistDTO fromWatchlist(Watchlist watchlist) {
        return new WatchlistDTO(
                watchlist.getViewer() != null ? watchlist.getViewer().getName() : null,
                watchlist.getContents().stream()
                        .map(ContentSummaryDTO::fromContent)
                        .toList());
    }
}
