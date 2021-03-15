package pl.pss.PSS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pss.PSS.model.Role;

@Repository
public interface RoleDelegation extends JpaRepository<Role, Long> {
}
