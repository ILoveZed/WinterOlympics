package org.informatics.winterolympics.repository;

import org.informatics.winterolympics.model.OlympicGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OlympicGameRepository extends JpaRepository<OlympicGame, Long> {
}
