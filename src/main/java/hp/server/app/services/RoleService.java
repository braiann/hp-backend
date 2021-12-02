package hp.server.app.services;

import hp.server.app.models.entity.Role;
import nrt.common.microservice.services.CommonService;

public interface RoleService extends CommonService<Role> {

    public Role getByDescription(String description);
}
