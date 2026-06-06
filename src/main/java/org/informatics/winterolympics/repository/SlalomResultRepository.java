package org.informatics.winterolympics.repository;

import org.informatics.winterolympics.model.SlalomResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SlalomResultRepository extends JpaRepository<SlalomResult, Long> {
    Optional<SlalomResult> findByRegistrationId(Long registrationId);
    List<SlalomResult> findByRegistrationSkiSlalomId(Long slalomId);
}
