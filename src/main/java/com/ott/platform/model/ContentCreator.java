package com.ott.platform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

/**
 * ContentCreator role – can upload content, update metadata, view content status.
 */
@Entity
@Table(name = "content_creators")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter @Setter
@NoArgsConstructor
public class ContentCreator extends User {

    @JsonIgnore
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Content> uploadedContent = new ArrayList<>();

    public ContentCreator(String name, String email, String password) {
        super(name, email, password, "CONTENT_CREATOR");
    }
}
