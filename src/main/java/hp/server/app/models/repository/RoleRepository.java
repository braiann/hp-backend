package hp.server.app.models.repository;

import hp.server.app.models.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    public Role findByDescription(String description);
}
