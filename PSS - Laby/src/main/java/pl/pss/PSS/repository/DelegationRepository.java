package pl.pss.PSS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.pss.PSS.model.Delegation;

import java.util.List;

@Repository
public interface DelegationRepository extends JpaRepository<Delegation, Long> {
    @Query("SELECT d FROM Delegation d WHERE d.delegant.userId = :userId")
    List<Delegation> findDelegationsByUser(@Param("userId") Long userId);
}
