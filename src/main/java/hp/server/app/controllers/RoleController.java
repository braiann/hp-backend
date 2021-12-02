package hp.server.app.controllers;

import hp.server.app.models.entity.Role;
import hp.server.app.services.RoleService;
import nrt.common.microservice.controllers.CommonController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/role")
public class RoleController extends CommonController<Role, RoleService> {

}
