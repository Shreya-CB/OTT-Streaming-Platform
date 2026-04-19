package com.ott.platform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

/**
 * Moderator role – reviews submitted content, approves/rejects/flags policy violations.
 */
@Entity
@Table(name = "moderators")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter @Setter
@NoArgsConstructor
public class Moderator extends User {

    @JsonIgnore
    @OneToMany(mappedBy = "moderator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    public Moderator(String name, String email, String password) {
        super(name, email, password, "MODERATOR");
    }
}
