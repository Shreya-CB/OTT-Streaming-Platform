package com.ott.platform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

/**
 * Core content entity.
 *
 * SOLID – SRP: Content stores data only.
 *   Lifecycle operations (publish, archive) live in ContentService (Pure Fabrication).
 * GRASP – Creator: ContentCreator creates Content objects.
 */
@Entity
@Table(name = "contents")
@Getter @Setter
@NoArgsConstructor
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentStatus status = ContentStatus.DRAFT;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creator_id")
    private ContentCreator creator;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "metadata_id")
    private Metadata metadata;

    @JsonIgnore
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WatchHistory> watchHistories = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RatingReview> ratings = new ArrayList<>();

    public Content(String title, ContentCreator creator) {
        this.title   = title;
        this.creator = creator;
        this.status  = ContentStatus.DRAFT;
    }
}
