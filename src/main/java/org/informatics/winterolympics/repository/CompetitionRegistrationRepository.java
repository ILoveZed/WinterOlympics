package org.informatics.winterolympics.repository;

import org.informatics.winterolympics.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetitionRegistrationRepository extends JpaRepository<CompetitionRegistration, Long> {
    List<CompetitionRegistration> findByUserKeycloakUserId(String keycloakUserId);
    boolean existsByUserAndSkiSlalom(User user, SkiSlalom skiSlalom);
    boolean existsByUserAndBiathlon(User user, Biathlon biathlon);
    List<CompetitionRegistration> findBySkiSlalomIdAndStatus(Long skiSlalomId, RegistrationStatus status);
    List<CompetitionRegistration> findByBiathlonIdAndStatus(Long biathlonId, RegistrationStatus status);
}
