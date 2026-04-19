package com.ott.platform.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * A Viewer's star-rating and comment for a Content item.
 * One viewer can rate each content once; updates overwrite the previous rating.
 */
@Entity
@Table(name = "rating_reviews",
       uniqueConstraints = @UniqueConstraint(columnNames = {"viewer_id", "content_id"}))
@Getter @Setter
@NoArgsConstructor
public class RatingReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int rating;             // 1 – 5

    @Column(length = 1000)
    private String comment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "viewer_id")
    private Viewer viewer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "content_id")
    private Content content;

    public RatingReview(int rating, String comment, Viewer viewer, Content content) {
        this.rating  = rating;
        this.comment = comment;
        this.viewer  = viewer;
        this.content = content;
    }
}
