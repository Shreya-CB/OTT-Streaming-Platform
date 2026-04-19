package com.ott.platform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

/**
 * Viewer role – can browse, stream, rate content, manage watchlist, subscribe.
 * Role-specific behaviour lives in WatchService and SubscriptionService (SRP).
 */
@Entity
@Table(name = "viewers")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter @Setter
@NoArgsConstructor
public class Viewer extends User {

    @JsonIgnore
    @OneToMany(mappedBy = "viewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WatchHistory> watchHistory = new ArrayList<>();

    @JsonIgnore
    @OneToOne(mappedBy = "viewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Watchlist watchlist;

    @JsonIgnore
    @OneToMany(mappedBy = "viewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Subscription> subscriptions = new ArrayList<>();

    public Viewer(String name, String email, String password) {
        super(name, email, password, "VIEWER");
    }
}
