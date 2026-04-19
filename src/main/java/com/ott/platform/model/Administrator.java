package com.ott.platform.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Administrator role – manages users, subscription plans, analytics, and reports.
 */
@Entity
@Table(name = "administrators")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter @Setter
@NoArgsConstructor
public class Administrator extends User {

    public Administrator(String name, String email, String password) {
        super(name, email, password, "ADMINISTRATOR");
    }
}
