package com.ott.platform.pattern.strategy;

import com.ott.platform.model.*;
import com.ott.platform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ╔══════════════════════════════════════════════════════════╗
 * ║  DESIGN PATTERN 2: STRATEGY (Behavioral)               ║
 * ║  Encapsulates interchangeable recommendation algorithms. ║
 * ║  New algorithms can be added without modifying callers.  ║
 * ║                                                          ║
 * ║  OCP + DIP: callers depend on this interface, not on     ║
 * ║  concrete implementations.                               ║
 * ╚══════════════════════════════════════════════════════════╝
 */
public interface RecommendationStrategy {
    /**
     * Returns a list of recommended Content for the given Viewer.
     */
    List<Content> recommend(Viewer viewer);
}
