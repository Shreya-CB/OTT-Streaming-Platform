package com.ott.platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

/**
 * A Viewer's personal watchlist – many-to-many with Content.
 */
@Entity
@Table(name = "watchlists")
@Getter @Setter
@NoArgsConstructor
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long watchlistId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "viewer_id")
    private Viewer viewer;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "watchlist_content",
        joinColumns        = @JoinColumn(name = "watchlist_id"),
        inverseJoinColumns = @JoinColumn(name = "content_id")
    )
    private List<Content> contents = new ArrayList<>();

    public Watchlist(Viewer viewer) {
        this.viewer = viewer;
    }
}
