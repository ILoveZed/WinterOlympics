package org.informatics.winterolympics.repository;

import org.informatics.winterolympics.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
