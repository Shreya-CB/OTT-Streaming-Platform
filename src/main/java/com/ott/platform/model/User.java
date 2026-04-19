package com.ott.platform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

/**
 * Base entity for all user types.
 *
 * SOLID – LSP: subclasses (Viewer, ContentCreator, Moderator, Administrator)
 * hold only role-specific fields; common attributes live here so any User
 * subtype can be substituted without breaking contracts.
 *
 * Uses JOINED inheritance → separate DB table per subtype.
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    /** Discriminator stored explicitly so the REST layer can read role without joins. */
    @Column(nullable = false)
    private String userType;

    protected User(String name, String email, String password, String userType) {
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.userType = userType;
    }
}
