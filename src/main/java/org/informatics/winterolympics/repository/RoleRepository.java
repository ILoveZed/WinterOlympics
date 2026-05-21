package org.informatics.winterolympics.repository;

import org.informatics.winterolympics.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
