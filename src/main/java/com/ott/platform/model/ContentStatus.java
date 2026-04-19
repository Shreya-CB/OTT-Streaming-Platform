package com.ott.platform.model;

/**
 * States from the State Diagram – Content Upload & Moderation.
 * Transitions: DRAFT → SUBMITTED → UNDER_REVIEW → APPROVED/REJECTED/FLAGGED → PUBLISHED
 */
public enum ContentStatus {
    DRAFT,
    SUBMITTED,
    UNDER_REVIEW,
    APPROVED,
    REJECTED,
    FLAGGED,
    PUBLISHED
}
