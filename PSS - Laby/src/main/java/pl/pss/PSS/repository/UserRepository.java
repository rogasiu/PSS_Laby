package pl.pss.PSS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pss.PSS.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
