# OTT Platform

## Overview

A comprehensive OTT (Over-The-Top) streaming platform backend management system built with Spring Boot. This project implements a full-featured content streaming platform with user roles (Viewers, Content Creators, Moderators, Administrators), content upload and moderation workflows, recommendation systems, and subscription management. The application follows SOLID principles and incorporates various design patterns for maintainable and scalable code.

## Features

- **User Management**: Registration, login, and role-based access control (Viewer, Content Creator, Moderator, Administrator)
- **Content Management**: Upload, review, moderation, and publishing of streaming content
- **Moderation System**: Flagged content review with approve/reject decisions and resubmit functionality
- **Recommendation Engine**: Genre-based and ML-based recommendation strategies using the Strategy pattern
- **Subscription Management**: Plans, payments, and status tracking
- **Watch History & Watchlists**: User activity tracking and personalized lists
- **Notifications**: System notifications for users
- **Ratings & Reviews**: Content rating and review system
- **Facade Pattern**: Simplified content upload process
- **Factory Pattern**: User creation based on roles

## Technologies Used

- **Backend**: Spring Boot 3.2.0, Java 21
- **Database**: MySQL (production) / H2 (development)
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven
- **Frontend**: HTML, CSS, JavaScript (single-page application)
- **Design Patterns**: Strategy, Facade, Factory
- **Principles**: SOLID (Single Responsibility, Open-Closed, Liskov Substitution, Interface Segregation, Dependency Inversion), GRASP (Controller, Creator, Pure Fabrication)

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- MySQL 8.0+ (for production profile)
- Git

## Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Shreya-CB/OTT-Streaming-Platform.git
   cd OTT-Streaming-Platform
   ```

2. **Build the project**:
   ```bash
   mvn clean install
   ```

3. **Database Setup**:
   - For development (H2 embedded): No setup required
   - For production (MySQL): Create a MySQL database and update `src/main/resources/application-mysql.properties` with your credentials

4. **Run the application**:
   - Development (H2):
     ```bash
     mvn spring-boot:run
     ```
   - Production (MySQL):
     ```bash
     mvn spring-boot:run -Dspring-boot.run.profiles=mysql
     ```

5. **Access the application**:
   - Frontend: http://localhost:8080
   - API Base URL: http://localhost:8080/api

## Usage

### User Roles and Workflows

- **Viewer**: Browse content, watch videos, rate/review, manage watchlists and subscriptions
- **Content Creator**: Upload content, track submission status, resubmit flagged content
- **Moderator**: Review flagged content, approve/reject submissions
- **Administrator**: Manage users, subscriptions, and system settings

### API Endpoints

#### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

#### Content Management
- `GET /api/content` - Get all published content
- `POST /api/content/upload` - Upload new content (Creators)
- `PUT /api/content/{id}/resubmit` - Resubmit edited content (Creators)
- `GET /api/content/moderation-queue` - Get moderation queue (Moderators)
- `POST /api/content/{id}/review` - Review content (Moderators)

#### Recommendations
- `GET /api/recommendations/{userId}` - Get personalized recommendations

#### User Management
- `GET /api/users/{id}` - Get user profile
- `PUT /api/users/{id}` - Update user profile

#### Subscriptions
- `GET /api/subscriptions` - Get available plans
- `POST /api/subscriptions` - Subscribe to a plan

For a complete API documentation, refer to the controller classes in `src/main/java/com/ott/platform/controller/`.

## Project Structure

```
src/
├── main/
│   ├── java/com/ott/platform/
│   │   ├── OttPlatformApplication.java          # Main application class
│   │   ├── controller/                           # REST controllers
│   │   ├── dto/                                  # Data Transfer Objects
│   │   ├── model/                                # JPA entities
│   │   ├── pattern/                              # Design patterns implementation
│   │   │   ├── facade/                           # Facade pattern
│   │   │   ├── factory/                          # Factory pattern
│   │   │   └── strategy/                         # Strategy pattern
│   │   ├── repository/                           # JPA repositories
│   │   └── service/                              # Business logic services
│   └── resources/
│       ├── application.properties                # Default config (H2)
│       ├── application-mysql.properties          # MySQL config
│       └── static/                               # Frontend files
└── test/                                         # Unit tests
```

## Design Patterns & Principles

### Creational Patterns
- **Factory Pattern**: `UserFactory` for creating different user types based on roles

### Structural Patterns
- **Facade Pattern**: `ContentUploadFacade` simplifies the content upload process across multiple services

### Behavioral Patterns
- **Strategy Pattern**: `RecommendationStrategy` interface with `GenreBasedRecommendation` and `MLRecommendation` implementations

### SOLID Principles
- **Single Responsibility**: Each service class handles one concern (e.g., `ContentService` for content operations)
- **Open-Closed**: Strategy pattern allows adding new recommendation algorithms without modifying existing code
- **Liskov Substitution**: Strategy implementations are interchangeable
- **Interface Segregation**: Separate interfaces for different concerns
- **Dependency Inversion**: Services depend on abstractions (interfaces) rather than concrete implementations

### GRASP Principles
- **Controller**: Controllers handle user input and delegate to services
- **Creator**: Services create domain objects
- **Pure Fabrication**: Service classes are fabricated to handle business logic

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Testing

Run tests with:
```bash
mvn test
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Authors

- **Team J11**: Developed as part of OOAD coursework

## Acknowledgments

- Spring Boot documentation
- Design Patterns literature
- Open-source community
