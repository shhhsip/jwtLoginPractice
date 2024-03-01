package pratice.jwtLogin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pratice.jwtLogin.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
