package org.informatics.winterolympics.repository;

import org.informatics.winterolympics.model.Biathlon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BiathlonRepository extends JpaRepository<Biathlon, Long> {
}
