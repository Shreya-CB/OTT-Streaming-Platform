package com.ott.platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

/**
 * Records when a Viewer last watched a Content item (streaming history).
 */
@Entity
@Table(name = "watch_history")
@Getter @Setter
@NoArgsConstructor
public class WatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate lastWatchedDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "viewer_id")
    private Viewer viewer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "content_id")
    private Content content;

    public WatchHistory(Viewer viewer, Content content) {
        this.viewer           = viewer;
        this.content          = content;
        this.lastWatchedDate  = LocalDate.now();
    }
}
