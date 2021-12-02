package hp.server.app.controllers;

import hp.server.app.models.entity.Person;
import hp.server.app.services.PersonService;
import nrt.common.microservice.controllers.CommonController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/person")
public class PersonController extends CommonController<Person, PersonService> {


}
