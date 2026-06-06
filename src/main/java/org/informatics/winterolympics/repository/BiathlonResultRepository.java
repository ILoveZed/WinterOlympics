package org.informatics.winterolympics.repository;

import org.informatics.winterolympics.model.BiathlonResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BiathlonResultRepository extends JpaRepository<BiathlonResult, Long> {
    Optional<BiathlonResult> findByRegistrationId(Long registrationId);
    List<BiathlonResult> findByRegistrationBiathlonId(Long biathlonId);
}
