package hp.server.app.models.repository;

import hp.server.app.models.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {

    public PasswordReset findByCode(String code);
}
