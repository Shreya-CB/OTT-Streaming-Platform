package com.ott.platform.repository;
import com.ott.platform.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientAndIsReadFalse(User recipient);
    List<Notification> findByRecipient(User recipient);
}
