package com.ott.platform.pattern.factory;

import com.ott.platform.model.*;
import org.springframework.stereotype.Component;

/**
 * ╔══════════════════════════════════════════════════════╗
 * ║  DESIGN PATTERN 1: FACTORY METHOD (Creational)      ║
 * ║  Creates the correct User subtype at runtime without ║
 * ║  exposing construction logic to callers.             ║
 * ║                                                      ║
 * ║  OCP: adding a new role = add one case here only.   ║
 * ╚══════════════════════════════════════════════════════╝
 */
@Component
public class UserFactory {

    /**
     * Factory method – returns a fully-constructed User subtype.
     *
     * @param name     display name
     * @param email    unique email
     * @param password raw password (hashing handled by UserService)
     * @param userType "VIEWER" | "CONTENT_CREATOR" | "MODERATOR" | "ADMINISTRATOR"
     */
    public User createUser(String name, String email, String password, String userType) {
        return switch (userType.toUpperCase()) {
            case "VIEWER"           -> new Viewer(name, email, password);
            case "CONTENT_CREATOR"  -> new ContentCreator(name, email, password);
            case "MODERATOR"        -> new Moderator(name, email, password);
            case "ADMINISTRATOR"    -> new Administrator(name, email, password);
            default -> throw new IllegalArgumentException(
                    "Unknown user type: " + userType +
                    ". Valid types: VIEWER, CONTENT_CREATOR, MODERATOR, ADMINISTRATOR");
        };
    }
}
