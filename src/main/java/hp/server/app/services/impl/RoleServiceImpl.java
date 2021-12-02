package hp.server.app.services.impl;

import hp.server.app.models.entity.Role;
import hp.server.app.models.repository.RoleRepository;
import hp.server.app.services.RoleService;
import nrt.common.microservice.services.impl.CommonServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends CommonServiceImpl<Role, RoleRepository> implements RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role getByDescription(String description) {
        logger.info("Ingresa a findByDescription");
        return roleRepository.findByDescription(description);
    }
}
