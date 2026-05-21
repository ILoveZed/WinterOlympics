package org.informatics.winterolympics.repository;

import org.informatics.winterolympics.model.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AthleteRepository extends JpaRepository<Athlete, Long> {
}
