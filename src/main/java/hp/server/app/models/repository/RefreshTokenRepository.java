package hp.server.app.models.repository;

import hp.server.app.models.entity.Person;
import hp.server.app.models.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    public Optional<RefreshToken> findById(Long id);

    public Optional<RefreshToken> findByToken(String token);

    @Modifying
    public int deleteByPerson(Person person);
}
