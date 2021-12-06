package hp.server.app.services.impl;

import hp.server.app.models.entity.Address;
import hp.server.app.models.entity.Person;
import hp.server.app.models.entity.Role;
import hp.server.app.models.repository.PersonRepository;
import hp.server.app.services.AddressService;
import hp.server.app.services.PersonService;
import hp.server.app.services.RoleService;
import hp.server.app.utils.StringUtil;
import hp.server.app.utils.exceptionsmessages.ApiRestErrorMessage;
import nrt.common.microservice.services.impl.CommonServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PersonServiceImpl extends CommonServiceImpl<Person, PersonRepository> implements PersonService {

    private static final Logger logger = LoggerFactory.getLogger(PersonServiceImpl.class);
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private AddressService addressService;

    @Override
    public Person save(Person entity) throws Exception {
        logger.info("Enter to save()");

        if (StringUtil.cadContainsDigit(entity.getFirstName()) || StringUtil.cadContainsDigit(entity.getLastName())) {
            throw new Exception(ApiRestErrorMessage.FIRSTNAME_OR_LASTNAME_CONTAINS_DIGITS);
        }

        LocalDateTime currentDay = LocalDateTime.now();
        if (entity.getBirthDate().isAfter(currentDay)) {
            throw new Exception(ApiRestErrorMessage.BIRTH_DATE_INVALID);
        }

        Person personByDocumentNumber = personRepository.findByDocumentNumber(entity.getDocumentNumber());
        if (personByDocumentNumber != null) {
            throw new Exception(ApiRestErrorMessage.PERSON_EXISTS_BY_DOCUMENT);
        }

        if (StringUtil.cadContainsLetters(entity.getPhoneNumber())) {
            throw new Exception(ApiRestErrorMessage.PHONE_NUMBER_INVALID);
        }

        // Valid if exists a person/user with email
        Person existsPerson = personRepository.findByEmail(entity.getEmail());
        if (existsPerson != null) {
            throw new Exception(ApiRestErrorMessage.EXISTS_PERSON_BY_EMAIL + entity.getEmail());
        }

        Optional<Person> exitsPersonByUsername = personRepository.findByUsername(entity.getUsername());
        if (exitsPersonByUsername.isPresent()) {
            throw new Exception(ApiRestErrorMessage.EXISTS_PERSON_BY_USERNAME);
        }

        // In this step find role and set to entity object before save
        Role role = entity.getRole();

        if (role == null) {
            Role roleUser = roleService.getByDescription(Role.ROLE_USER);
            entity.setRole(roleUser);
        } else {
            switch (role.getDescription()) {
                case "ROLE_ADMIN":
                    Role roleAdmin = roleService.getByDescription(Role.ROLE_ADMIN);
                    entity.setRole(roleAdmin);
                    break;

                case "ROLE_SUPERADMIN":
                    Role roleSuperAdmin = roleService.getByDescription(Role.ROLE_SUPERADMIN);
                    entity.setRole(roleSuperAdmin);
                    break;

                default:
                    Role roleUser = roleService.getByDescription(Role.ROLE_USER);
                    entity.setRole(roleUser);
                    break;
            }
        }

        if (entity.getAddress() != null) {
            Address addressDB = addressService.save(entity.getAddress());
            entity.setAddress(addressDB);
        }

        // TODO: encode password

        Person personDB = super.save(entity);
        // TODO: send email after registration if a registration is ok

        return personDB;
    }
}
