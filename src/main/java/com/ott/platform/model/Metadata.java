package com.ott.platform.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Stores descriptive metadata for a Content item.
 * Separated from Content following SRP (data vs lifecycle concerns).
 */
@Entity
@Table(name = "metadata")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Metadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String genre;
    private int duration;       // minutes
    private String ageRating;   // e.g. U, U/A 13+, A
    private String language;

    @Column(length = 1000)
    private String description;

    public Metadata(String genre, int duration, String ageRating, String language, String description) {
        this.genre       = genre;
        this.duration    = duration;
        this.ageRating   = ageRating;
        this.language    = language;
        this.description = description;
    }
}
