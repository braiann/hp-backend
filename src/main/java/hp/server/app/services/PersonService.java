package hp.server.app.services;

import hp.server.app.models.entity.Person;
import nrt.common.microservice.services.CommonService;

public interface PersonService extends CommonService<Person> {

    public Person getByEmail(String email);

    public void updatePersonPassword(Person person);
}
