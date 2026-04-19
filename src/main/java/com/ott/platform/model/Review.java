package com.ott.platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

/**
 * Stores a Moderator's review decision for a piece of Content.
 * Decision values: APPROVED | REJECTED | FLAGGED
 *
 * GRASP – Creator: Moderator creates Review.
 */
@Entity
@Table(name = "reviews")
@Getter @Setter
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(nullable = false)
    private String decision;        // APPROVED | REJECTED | FLAGGED

    @Column(length = 1000)
    private String comments;

    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "content_id")
    private Content content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "moderator_id")
    private Moderator moderator;

    public Review(String decision, String comments, Content content, Moderator moderator) {
        this.decision    = decision;
        this.comments    = comments;
        this.content     = content;
        this.moderator   = moderator;
        this.reviewedAt  = LocalDateTime.now();
    }
}
